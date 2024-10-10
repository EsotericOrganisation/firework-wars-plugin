package org.esoteric_organisation.firework_wars_plugin.scoreboard.wrapper;

import net.kyori.adventure.text.Component;
import org.esoteric_organisation.firework_wars_plugin.game.team.FireworkWarsTeam;
import org.esoteric_organisation.firework_wars_plugin.scoreboard.api.FastBoard;
import org.esoteric_organisation.firework_wars_plugin.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class FireworkWarsScoreboard {
    private final FastBoard fastBoard;

    private final List<Component> placeholderLines;
    private final List<Component> lines;

    private final Map<FireworkWarsTeam, Component> teamPlaceholderLines;
    private final Map<FireworkWarsTeam, Component> teamLines;

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

    public Map<FireworkWarsTeam, Component> getTeamPlaceholderLines() {
        return teamPlaceholderLines;
    }

    public Map<FireworkWarsTeam, Component> getTeamLines() {
        return teamLines;
    }

    public void setEndgameLine(Component endgameLine) {
        this.endgameLine = endgameLine;
    }

    public void setIncludeSecondEventLine(boolean includeSecondEventLine) {
        this.includeSecondEventLine = includeSecondEventLine;
    }

    public FireworkWarsScoreboard(FastBoard fastBoard, Map<FireworkWarsTeam, Component> teamLines) {
        this.fastBoard = fastBoard;

        this.placeholderLines = fastBoard.getLines();
        this.lines = new ArrayList<>(placeholderLines);

        this.teamPlaceholderLines = teamLines;
        this.teamLines = new HashMap<>(teamLines);
    }

    private List<Component> combineLines() {
        List<Component> result = new ArrayList<>();
        result.addAll(lines.subList(0, lines.indexOf(Component.empty()) + 1));
        result.addAll(teamLines.values());
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

    public FireworkWarsScoreboard setTeamLine(FireworkWarsTeam team, Component line) {
        teamLines.put(team, line);
        return this;
    }

    @SafeVarargs
    public final FireworkWarsScoreboard updateTeamLine(FireworkWarsTeam team, Pair<String, String>... replacements) {
        Component line = teamPlaceholderLines.get(team);

        for (Pair<String, String> replacement : replacements) {
            line = line.replaceText(config ->
                    config.match(replacement.getLeft()).replacement(replacement.getRight()));
        }

        return setTeamLine(team, line);
    }

    public void update() {
        fastBoard.updateLines(combineLines());
    }

    public void delete() {
        fastBoard.delete();
    }
}
