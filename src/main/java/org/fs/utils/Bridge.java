package org.fs.utils;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ClassGen;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionFactory;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.ReferenceType;
import com.sun.org.apache.bcel.internal.generic.Type;

/**
 * Force an object expose an interface at runtime, through a generated proxy class. It must
 * implement all required methods. Requires BCEL.
 * <p>
 * http://snippets.dzone.com/posts/show/4804
 *
 * @author tunah
 * @param <C>
 * @param <I>
 */
@SuppressWarnings("rawtypes")
public abstract class Bridge<C,I> {

	protected C	__target;	// the implementation of the interface

	@SuppressWarnings("unchecked")
	private final void __init(final Object target) {
		this.__target = (C)target;
	}

	// cache already-generated bridges
	private static HashMap <Class, HashMap <Class, Class>>	cache	= new HashMap <Class, HashMap <Class, Class>>();
	// allow injection of classes from byte arrays
	private static InjectingClassLoader						loader;
	static {
		AccessController.doPrivileged(new PrivilegedAction <Object>() {

			@Override
			public Object run() {
				loader = new InjectingClassLoader();
				return null;
			}
		});
	}

	private static final void cacheSet(final Class key1, final Class key2, final Class value) {
		HashMap <Class, Class> intermediate = cache.get(key1);
		if (intermediate == null) {
			cache.put(key1, intermediate = new HashMap <Class, Class>());
		}
		intermediate.put(key2, value);
	}

	private static final Class cacheGet(final Class key1, final Class key2) {
		final HashMap <Class, Class> intermediate = cache.get(key1);
		if (intermediate == null) return null;
		return intermediate.get(key2);
	}

	// returns [ [ifaceMethods...] [fromMethods...] ]
	private static Method[][] getMapping(final Class <?> from, final Class <?> iface)
			throws NoSuchMethodException, IllegalAccessException {
		if (!iface.isInterface()) throw new IllegalArgumentException(iface.getName() + " is not an interface");
		if (!java.lang.reflect.Modifier.isPublic(from.getModifiers()))
			throw new IllegalAccessException(from.getName() + " is not public");
		final java.lang.reflect.Method[][] map = new java.lang.reflect.Method[2][];
		map[0] = iface.getMethods();
		map[1] = new java.lang.reflect.Method[map[0].length];
		for (int i = 0; i < map[0].length; i++) {
			try {
				final Method match = from.getMethod(map[0][i].getName(), map[0][i].getParameterTypes());
				if (!map[0][i].getReturnType().isAssignableFrom(match.getReturnType()))
					throw new NoSuchMethodException("Return type " + match.getReturnType().getName() + " of "
							+ toString(match) + " is not compatible with return type "
							+ map[0][i].getReturnType().getName() + " of " + toString(map[0][i]));
				map[1][i] = match;
			} catch(final NoSuchMethodException e) {
				throw new NoSuchMethodException("Couldn't find " + toString(map[0][i]) + " in " + from.getName());
			}
		}
		return map;
	}

	private static String toString(final Method m) {
		final StringBuffer sb = new StringBuffer(m.getDeclaringClass().getName());
		sb.append(".").append(m.getName()).append("(");
		boolean first = true;
		for (final Class c : m.getParameterTypes()) {
			if (!first) {
				sb.append(", ");
			}
			sb.append(c.getName());
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}

	private static <C,I>Class <? extends Bridge <C, I>> create(final Class <C> from, final Class <I> iface) {
		try {
			final Method[][] map = getMapping(from, iface);
			final String bridgeName = "Bridge_" + from.getSimpleName() + "_" + iface.getSimpleName();
			// public class Bridge_Thing_Mungible extends Bridge<Thing,Mungible> implements Mungible
			final ClassGen cg = new ClassGen(bridgeName, Bridge.class.getName(), // superclass name
					"<generated>", // source file name
					Constants.ACC_PUBLIC | Constants.ACC_FINAL | Constants.ACC_SUPER, // flags
					new String[]{iface.getName()} // interfaces
			);
			final ConstantPoolGen cpg = cg.getConstantPool();
			final InstructionFactory factory = new InstructionFactory(cg, cpg);
			final ReferenceType targetType = (ReferenceType)Type.getType(from);
			for (int i = 0; i < map[0].length; i++) {
				final Method imeth = map[0][i];
				final Method fmeth = map[1][i];
				final Type ireturnType = Type.getType(imeth.getReturnType()), freturnType = Type.getType(fmeth.getReturnType());
				final Class[] iargs = imeth.getParameterTypes(), fargs = fmeth.getParameterTypes();
				final Type[] iargsT = new Type[iargs.length], fargsT = new Type[fargs.length];
				final String[] argNames = new String[iargs.length];
				for (int j = 0; j < iargs.length; j++) {
					iargsT[j] = Type.getType(iargs[j]);
					fargsT[j] = Type.getType(fargs[j]);
					argNames[j] = "arg" + i;
				}
				final InstructionList il = new InstructionList();
				// public int munge(Object arg0, int arg1) {
				final MethodGen mg = new MethodGen(Constants.ACC_PUBLIC, ireturnType, iargsT, argNames,
						imeth.getName(), bridgeName, il, cpg);
				// (Thing)this.__target
				il.append(new ALOAD(0));
				il.append(factory.createFieldAccess(Bridge.class.getName(), "__target", Type.OBJECT,
						Constants.GETFIELD));
				il.append(factory.createCheckCast(targetType));
				// .munge(arg0, arg1);
				int position = 1;
				for (int j = 0; j < iargs.length; j++) {
					il.append(InstructionFactory.createLoad(iargsT[j], position));
					position += iargsT[j].getSize();
				}
				il.append(factory.createInvoke(from.getName(), fmeth.getName(), freturnType, fargsT,
						from.isInterface() ? Constants.INVOKEINTERFACE : Constants.INVOKEVIRTUAL));
				// return (last result, if any)
				il.append(InstructionFactory.createReturn(ireturnType));
				mg.setMaxStack();
				cg.addMethod(mg.getMethod());
				il.dispose(); // Allow instruction handles to be reused
			}
			// public Bridge_Thing_Mungible() { super(); }
			cg.addEmptyConstructor(Constants.ACC_PUBLIC);
			try {
				cg.getJavaClass().dump("proxy.class");
			} catch(final Exception e) {
				throw new RuntimeException(e);
			}
			final byte[] classData = cg.getJavaClass().getBytes();
			@SuppressWarnings("unchecked")
			final Class <Bridge <C, I>> c = loader.load(cg.getClassName(), classData);
			return c;
		} catch(final IllegalAccessException e) {
			throw new BridgeFailure(from, iface, e);
		} catch(final NoSuchMethodException e) {
			throw new BridgeFailure(from, iface, e);
		}
	}

	@SuppressWarnings("unchecked")
	private static <C,I>Class <? extends Bridge <C, I>> get(final Class <C> from, final Class <I> iface) {
		// Pair<Class,Class> key = new Pair<Class,Class>(from, iface);
		Class <? extends Bridge <C, I>> bridgeClass = cacheGet(from, iface);
		if (bridgeClass == null) {
			bridgeClass = create(from, iface);
			cacheSet(from, iface, bridgeClass);
		}
		return bridgeClass;
	}

	/**
	 * Expose interface iface by creating a proxy.
	 * <p>
	 * The type of target must be from, a subclass of from, or a class implementing from.
	 * <p>
	 * from must be public, and must expose all methods in iface.
	 */
	@SuppressWarnings({"unchecked", "javadoc"})
	public static <I>I expose(final Object target, final Class from, final Class <I> iface) {
		try {
			final Class <? extends Bridge <?, I>> c = Bridge.get(from, iface);
			final Bridge <?, I> proxy = c.newInstance();
			proxy.__init(target);
			return (I)proxy;
		} catch(final InstantiationException e) {
			throw new RuntimeException(e);
		} catch(final IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Expose interface iface by creating a proxy.
	 * <p>
	 * Tries expose(target, target.getClass(), iface) first.
	 * <p>
	 * Then works up the class hierarchy. If doesn't find a public superclass
	 * <p>
	 * That exposes all methods, it tries interfaces.
	 * <p>
	 * If you know in advance the class or interface that exposes the needed methods, use
	 * expose(target, Class.forName("ExposingClass"), iface).
	 */
	@SuppressWarnings("javadoc")
	public static <I>I expose(final Object target, final Class <I> iface) {
		final Class tClass = target.getClass();
		Class from = tClass;
		do {
			try {
				final I result = expose(target, from, iface);
				if (tClass != from) {
					cacheSet(tClass, iface, result.getClass());
				}
				return result;
			} catch(final BridgeFailure e) {
				final Throwable cause = e.getCause();
				if (cause instanceof NoSuchMethodException) {
					// have traced superclass up until the interface isn't satisfied.
					// try interfaces and then give up.
					// in the case of a private implementation of a public interface,
					// this allows you to subset the interface.
					final Class[] targetIfaces = target.getClass().getInterfaces();
					for (final Class targetIface : targetIfaces) {
						try {
							final I result = expose(target, targetIface, iface);
							cacheSet(tClass, iface, result.getClass()); // perf hack
							return result;
							// ignore, reported exception is that for class hierarchy.
						} catch(final BridgeFailure ex) {}
					}
					throw new BridgeFailure(target, iface, e);
				} else if (!(cause instanceof IllegalAccessException)) throw e;
			}
			from = from.getSuperclass();
		} while (from != null);
		throw new RuntimeException("Object " + target + " (" + target.getClass().getName()
				+ ") has no public superclass");
	}
}

@SuppressWarnings("rawtypes")
class BridgeFailure extends RuntimeException {

	private static final long	serialVersionUID	= 1L;

	public BridgeFailure(final Class from, final Class iface, final Exception cause) {
		super("Could not map class " + from.getName() + " to interface " + iface.getName());
		initCause(cause);
	}

	public BridgeFailure(final Object target, final Class iface, final Exception cause) {
		super("Could not map first public supertype of " + target + " (" + target.getClass().getName() + "),"
				+ " or any implemented interface, to interface " + iface.getName());
		initCause(cause);
	}
}

// ClassLoader implementation to let you load classes from byte arrays
@SuppressWarnings("rawtypes")
class InjectingClassLoader extends ClassLoader {

	public InjectingClassLoader() {
		super(InjectingClassLoader.class.getClassLoader());
	}

	public Class load(final String name, final byte[] buffer) {
		return defineClass(name, buffer, 0, buffer.length);
	}
}
