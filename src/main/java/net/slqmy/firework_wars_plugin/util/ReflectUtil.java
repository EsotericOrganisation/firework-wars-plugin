package net.slqmy.firework_wars_plugin.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.slqmy.firework_wars_plugin.FireworkWarsPlugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.function.Supplier;

@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public final class ReflectUtil {
    private static boolean reflecting;

    private static Class<?> clazz;
    private static Object instance;

    public static Field getField(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            FireworkWarsPlugin.LOGGER.warning("Failed to get field:" + e.getMessage());
        }

        throw new RuntimeException("Failed to get field!");
    }

    public static Field getField(String fieldName) {
        if (!reflecting) {
            throw new IllegalStateException("Cannot get field without class context outside a reflection function!");
        }

        return getField(clazz, fieldName);
    }

    @SuppressWarnings("unchecked")
    public static <T> T getFieldValue(Field field, Object instance) {
        try {
            return (T) field.get(instance);
        } catch (IllegalAccessException e) {
            FireworkWarsPlugin.LOGGER.warning("Failed to get field value:" + e.getMessage());
        }

        throw new RuntimeException("Failed to get field value!");
    }

    public static <T> T getFieldValue(Field field) {
        if (!reflecting) {
            throw new IllegalStateException("Cannot get field value without class context outside a reflection function!");
        }

        return getFieldValue(field, instance);
    }

    public static <T> T getFieldValue(Class<?> clazz, String fieldName, Object instance) {
        Field field = getField(clazz, fieldName);
        return getFieldValue(field, instance);
    }

    public static <T> T getFieldValue(String fieldName) {
        if (!reflecting) {
            throw new IllegalStateException("Cannot get field value without class context outside a reflection function!");
        }

        return getFieldValue(clazz, fieldName, instance);
    }

    public static void setFieldValue(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            FireworkWarsPlugin.LOGGER.warning("Failed to set field value:" + e.getMessage());
        }
    }

    public static void setFieldValue(Field field, Object value) {
        if (!reflecting) {
            throw new IllegalStateException("Cannot set field value without class context outside a reflection function!");
        }

        setFieldValue(field, instance, value);
    }

    public static void setFieldValue(Class<?> clazz, String fieldName, Object instance, Object value) {
        Field field = getField(clazz, fieldName);
        setFieldValue(field, instance, value);
    }

    public static void setFieldValue(String fieldName, Object value) {
        if (!reflecting) {
            throw new IllegalStateException("Cannot set field value without class context outside a reflection function!");
        }

        setFieldValue(clazz, fieldName, instance, value);
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            FireworkWarsPlugin.LOGGER.warning("Failed to get method:" + e.getMessage());
        }

        throw new RuntimeException("Failed to get method!");
    }

    public static Method getMethod(String methodName, Class<?>... parameterTypes) {
        if (!reflecting) {
            throw new IllegalStateException("Cannot get method without class context outside a reflection function!");
        }

        return getMethod(clazz, methodName, parameterTypes);
    }

    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(Method method, Object instance, Object... args) {
        try {
            return (T) method.invoke(instance, args);
        } catch (Exception e) {
            FireworkWarsPlugin.LOGGER.warning("Failed to invoke method:" + e.getMessage());
        }

        throw new RuntimeException("Failed to invoke method!");
    }

    public static <T> T invokeMethod(Method method, Object... args) {
        if (!reflecting) {
            throw new IllegalStateException("Cannot invoke method without class context outside a reflection function!");
        }

        return invokeMethod(method, instance, args);
    }

    public static <T> T invokeMethod(Class<?> clazz, String methodName, Class<?>[] parameterTypes, Object instance, Object... args) {
        Method method = getMethod(clazz, methodName, parameterTypes);
        return invokeMethod(method, instance, args);
    }

    public static <T> T invokeMethod(String methodName, Class<?>[] parameterTypes, Object... args) {
        if (!reflecting) {
            throw new IllegalStateException("Cannot invoke method without class context outside a reflection function!");
        }

        return invokeMethod(clazz, methodName, parameterTypes, instance, args);
    }

    public static void reflect(Class<?> clazz, Object instance, Runnable runnable) {
        ReflectUtil.reflecting = true;
        ReflectUtil.clazz = clazz;
        ReflectUtil.instance = instance;

        runnable.run();

        ReflectUtil.reflecting = false;
        ReflectUtil.clazz = null;
        ReflectUtil.instance = null;
    }

    public static <T> T reflect(Class<?> clazz, Object instance, Supplier<T> supplier) {
        ReflectUtil.reflecting = true;
        ReflectUtil.clazz = clazz;
        ReflectUtil.instance = instance;

        T value = supplier.get();

        ReflectUtil.reflecting = false;
        ReflectUtil.clazz = null;
        ReflectUtil.instance = null;

        return value;
    }
}
