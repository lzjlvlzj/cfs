package org.ack.cfs.util;

import java.lang.reflect.Field;

import org.apache.ibatis.mapping.BoundSql;


public class ReflectUtil {

	public static Object getFieldValue(
			Object obj, String fieldName) {
		  Object result = null;
          Field field =getField(obj, fieldName);
          if (field != null) {
             field.setAccessible(true);
             try {
                 result = field.get(obj);
             } catch (IllegalArgumentException e) {
                 e.printStackTrace();
             } catch (IllegalAccessException e) {
                 e.printStackTrace();
             }
          }
          return result;
	}

	public static Field getField(Object obj, String fieldName) {
		
		Field field = null;
        for (Class<?> clazz=obj.getClass(); clazz != Object.class; clazz=clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                //这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
            }
         }
         return field;
	}

	public static void setFieldValue(BoundSql obj, String fieldName,
			String pageSql) {
		Field field = getField(obj, fieldName);
        if (field != null) {
           try {
               field.setAccessible(true);
               field.set(obj, pageSql);
           } catch (IllegalArgumentException e) {
               e.printStackTrace();
           } catch (IllegalAccessException e) {
               e.printStackTrace();
           }
        }
    }
	

}
