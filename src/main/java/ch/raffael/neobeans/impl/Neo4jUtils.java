package ch.raffael.neobeans.impl;

import java.util.Arrays;

import ch.raffael.util.common.UnreachableCodeException;


/**
 * @author <a href="mailto:herzog@raffael.ch">Raffael Herzog</a>
 */
public class Neo4jUtils {

    private Neo4jUtils() {
    }

    public static boolean equals(Object a, Object b) {
        if ( a == null ) {
            return b == null;
        }
        else if ( b == null ) {
            return false;
        }
        else if ( a.getClass().isArray() ) {
            if ( !b.getClass().isArray() ) {
                return false;
            }
            if ( a.getClass().getComponentType().isPrimitive() ) {
                if ( !b.getClass().getComponentType().equals(a.getClass().getComponentType()) ) {
                    return false;
                }
                else if ( a.getClass().getComponentType().equals(int.class) ) {
                    return Arrays.equals((int[])a, (int[])b);
                }
                else if ( a.getClass().getComponentType().equals(long.class) ) {
                    return Arrays.equals((long[])a, (long[])b);
                }
                else if ( a.getClass().getComponentType().equals(short.class) ) {
                    return Arrays.equals((short[])a, (short[])b);
                }
                else if ( a.getClass().getComponentType().equals(byte.class) ) {
                    return Arrays.equals((byte[])a, (byte[])b);
                }
                else if ( a.getClass().getComponentType().equals(boolean.class) ) {
                    return Arrays.equals((boolean[])a, (boolean[])b);
                }
                else if ( a.getClass().getComponentType().equals(char.class) ) {
                    return Arrays.equals((char[])a, (char[])b);
                }
                else if ( a.getClass().getComponentType().equals(double.class) ) {
                    return Arrays.equals((double[])a, (double[])b);
                }
                else if ( a.getClass().getComponentType().equals(float.class) ) {
                    return Arrays.equals((float[])a, (float[])b);
                }
                else {
                    throw new UnreachableCodeException();
                }
            }
            else if ( b.getClass().getComponentType().isPrimitive() ) {
                return false;
            }
            else {
                return Arrays.equals((Object[])a, (Object[])b);
            }
        }
        else {
            return a.equals(b);
        }
    }

}
