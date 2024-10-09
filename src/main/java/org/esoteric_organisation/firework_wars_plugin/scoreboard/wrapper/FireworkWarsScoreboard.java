package org.esoteric_organisation.firework_wars_plugin.scoreboard.wrapper;

import net.kyori.adventure.text.Component;
import org.esoteric_organisation.firework_wars_plugin.scoreboard.api.FastBoard;
import org.esoteric_organisation.firework_wars_plugin.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class FireworkWarsScoreboard {
    private final FastBoard fastBoard;

    private final List<Component> placeholderLines;
    private final List<Component> lines;

    private final List<Component> teamPlaceholderLines;
    private final List<Component> teamLines;

    private Component endgameLine;
    private boolean includeSecondEventLine;

    public FastBoard getFastBoard() {
        return fastBoard;
    }

    public List<Component> getPlaceholderLines() {
        return placeholderLines;
    }

    public List<Component> getLines() {
        return lines;
    }

    public List<Component> getTeamPlaceholderLines() {
        return teamPlaceholderLines;
    }

    public List<Component> getTeamLines() {
        return teamLines;
    }

    public void setEndgameLine(Component endgameLine) {
        this.endgameLine = endgameLine;
    }

    public void setIncludeSecondEventLine(boolean includeSecondEventLine) {
        this.includeSecondEventLine = includeSecondEventLine;
    }

    public FireworkWarsScoreboard(FastBoard fastBoard) {
        this.fastBoard = fastBoard;

        this.placeholderLines = getLines(fastBoard);
        this.lines = new ArrayList<>(placeholderLines);

        this.teamPlaceholderLines = getTeamLines(fastBoard);
        this.teamLines = new ArrayList<>(teamPlaceholderLines);
    }

    private List<Component> getLines(FastBoard fastBoard) {
        List<Component> lines = fastBoard.getLines();
        List<Component> result = new ArrayList<>();

        result.addAll(lines.subList(0, lines.indexOf(Component.empty()) + 1));
        result.addAll(lines.subList(lines.lastIndexOf(Component.empty()), lines.size()));
        return result;
    }

    private List<Component> getTeamLines(FastBoard fastBoard) {
        List<Component> lines = fastBoard.getLines();
        return lines.subList(lines.indexOf(Component.empty()) + 1, lines.lastIndexOf(Component.empty()));
    }

    private List<Component> combineLines() {
        List<Component> result = new ArrayList<>();
        result.addAll(lines.subList(0, lines.indexOf(Component.empty()) + 1));
        result.addAll(teamLines);
        result.addAll(lines.subList(lines.lastIndexOf(Component.empty()), lines.size()));

        if (includeSecondEventLine) {
            result.add(2, endgameLine);
        }

        return result;
    }

    public FireworkWarsScoreboard setLine(int index, Component line) {
        lines.set(index, line);
        return this;
    }

    @SafeVarargs
    public final FireworkWarsScoreboard updateLine(int index, Pair<String, String>... replacements) {
        Component line = placeholderLines.get(index);

        for (Pair<String, String> replacement : replacements) {
            line = line.replaceText(config ->
                config.match(replacement.getLeft()).replacement(replacement.getRight()));
        }

        return setLine(index, line);
    }

    public FireworkWarsScoreboard setTeamLine(int index, Component line) {
        teamLines.set(index, line);
        return this;
    }

    @SafeVarargs
    public final FireworkWarsScoreboard updateTeamLine(int index, Map.Entry<String, String>... replacements) {
        Component line = teamPlaceholderLines.get(index);

        for (Map.Entry<String, String> replacement : replacements) {
            line = line.replaceText(config ->
                config.match(replacement.getKey()).replacement(replacement.getValue()));
        }

        return setTeamLine(index, line);
    }

    public void update() {
        fastBoard.updateLines(combineLines());
    }

    public void delete() {
        fastBoard.delete();
    }
}
