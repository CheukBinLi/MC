

public interface ApplicationContext {

	<T> T getBeans(String name) throws Throwable;

}
