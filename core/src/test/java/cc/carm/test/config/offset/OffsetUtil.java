//package config.offset;
//
//
//import sun.misc.Unsafe;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Modifier;
//import java.util.Collections;
//import java.util.LinkedList;
//import java.util.List;
//
/// **
// * @author Chris2018998
// */
//public class OffsetUtil {
//    private static Unsafe unsafe;
//
//    static {
//        try {
//            // 获取 Unsafe 内部的私有的实例化单例对象
//            Field field = Unsafe.class.getDeclaredField("theUnsafe");
//            // 无视权限
//            field.setAccessible(true);
//            unsafe = (Unsafe) field.get(null);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//        }
/// /        try {
/// /            unsafe = AccessController.doPrivileged((PrivilegedExceptionAction<Unsafe>) () -> {
/// /                Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
/// /                theUnsafe.setAccessible(true);
/// /                return (Unsafe) theUnsafe.get(null);
/// /            });
/// /        } catch (Throwable e) {
/// /            System.err.println("Unable to load unsafe");
/// /        }
//    }
//
//    public static List<FieldOffset> getClassMemberOffset(Class<?> beanClass) {
//        List<FieldOffset> offsetsList = new LinkedList<>();
//        for (Field field : beanClass.getDeclaredFields()) {
//            FieldOffset fieldOffset = new FieldOffset(field);
//            offsetsList.add(fieldOffset);
//            if (Modifier.isStatic(field.getModifiers()))
//                fieldOffset.setOffsetValue(unsafe.staticFieldOffset(field));
//            else
//                fieldOffset.setOffsetValue(unsafe.objectFieldOffset(field));
//            Class<?> fieldType = field.getType();
//            if (!fieldType.getName().startsWith("java")) {
//                Field[] subfields = fieldType.getDeclaredFields();
//                if (subfields.length > 0) {
//                    fieldOffset.setSubFieldOffsetList(getClassMemberOffset(fieldType));
//                }
//            }
//        }
//
//        Collections.sort(offsetsList);
//        return offsetsList;
//    }
//
//
//}
//
