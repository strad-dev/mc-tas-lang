package plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/** Reads M7's state by reflection; M7 is not a dependency. */
public class M7Link {
	private static final Map<String, Member> members = new HashMap<>();

	/** Static field, or no-arg static method, {@code member} of M7's {@code className}; null without M7. */
	public static Object get(String className, String member) {
		Plugin m7 = Bukkit.getPluginManager().getPlugin("m7");
		if(m7 == null) return null;
		ClassLoader loader = m7.getClass().getClassLoader();
		String key = className + "#" + member;
		try {
			Member m = members.get(key);
			// A reloaded M7 is a new class loader.
			if(m == null || m.getDeclaringClass().getClassLoader() != loader) {
				m = resolve(Class.forName(className, true, loader), member);
				members.put(key, m);
			}
			return m instanceof Field f ? f.get(null) : ((Method) m).invoke(null);
		} catch(ReflectiveOperationException e) {
			return null;
		}
	}

	public static Object call(String className, String method, Class<?> argType, Object arg) {
		Plugin m7 = Bukkit.getPluginManager().getPlugin("m7");
		if(m7 == null) return null;
		ClassLoader loader = m7.getClass().getClassLoader();
		String key = className + "#" + method + "(" + argType.getName() + ")";
		try {
			Member m = members.get(key);
			if(m == null || m.getDeclaringClass().getClassLoader() != loader) {
				Method found = Class.forName(className, true, loader).getDeclaredMethod(method, argType);
				found.setAccessible(true);
				m = found;
				members.put(key, m);
			}
			return ((Method) m).invoke(null, arg);
		} catch(ReflectiveOperationException e) {
			return null;
		}
	}

	/** M7's class {@code className}, or null without M7. */
	public static Class<?> loadClass(String className) {
		Plugin m7 = Bukkit.getPluginManager().getPlugin("m7");
		if(m7 == null) return null;
		try {
			return Class.forName(className, true, m7.getClass().getClassLoader());
		} catch(ClassNotFoundException e) {
			return null;
		}
	}

	public static int getInt(String className, String member, int fallback) {
		return get(className, member) instanceof Integer i ? i : fallback;
	}

	public static boolean getBoolean(String className, String member) {
		return get(className, member) instanceof Boolean b && b;
	}

	private static Member resolve(Class<?> c, String member) throws ReflectiveOperationException {
		try {
			Field f = c.getDeclaredField(member);
			f.setAccessible(true);
			return f;
		} catch(NoSuchFieldException e) {
			Method m = c.getDeclaredMethod(member);
			m.setAccessible(true);
			return m;
		}
	}
}
