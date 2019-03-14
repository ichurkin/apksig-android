/*
 * Copyright (C) 2018-2019 Ivan Churkin ivan.churkin@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.apksig.icutils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * Helper class to find declared annotations
 */
public final class Annotations {

    public static <A extends Annotation> A getDeclaredAnnotation(Field field, Class<A> annotationClass) {
        Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
        return processAnnotations(annotationClass, declaredAnnotations);
    }


    public static <A extends Annotation> A getDeclaredAnnotation(Class containerClass, Class<A> annotationClass) {
        Annotation[] declaredAnnotations = containerClass.getDeclaredAnnotations();
        return processAnnotations(annotationClass, declaredAnnotations);
    }

    private static <A extends Annotation> A processAnnotations(Class<A> annotationClass, Annotation[] declaredAnnotations) {
        if (declaredAnnotations.length == 0) {
            return null;
        }
        for (Annotation a : declaredAnnotations) {
            if (a.annotationType().equals(annotationClass)) {
                return (A) a;
            }
        }
        return null;
    }


}
