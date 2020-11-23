package com.jirvan.reflection;

import java.lang.reflect.*;

public class JiReflection {

    public static <T> T newInstance(String classname, Object... initargs) {
        try {
            return newInstance((Class<? extends T>) Class.forName(classname), initargs);
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundRuntimeException(e);
        }
    }

    public static <T> T newInstance(Class<? extends T> clazz, Object... initargs) {
        try {
            if (initargs != null && initargs.length > 0) {
                Class<?>[] parameterTypes = new Class<?>[initargs.length];
                for (int i = 0; i < parameterTypes.length; i++) {
                    parameterTypes[i] = initargs[i].getClass();
                }
                return clazz.getConstructor(parameterTypes).newInstance(initargs);
            } else {
                return clazz.getConstructor().newInstance();
            }
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new InvocationTargetRuntimeException(e);
        } catch (InstantiationException e) {
            throw new InstantiationRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalAccessRuntimeException(e);
        }
    }

}
