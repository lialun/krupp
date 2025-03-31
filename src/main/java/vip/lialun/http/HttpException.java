package vip.lialun.http;

/**
 * HTTP请求异常。
 * 对所有HTTP异常进行了封装,使异常更容易进行捕获和处理。
 * 由于所有HttpException的抛出都是由其他异常被抛出引起的,所以此异常的堆栈信息并没有意义,故未打出堆栈。
 *
 * @author LiALuN
 */
public class HttpException extends RuntimeException {
    public HttpException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpException(String message) {
        super(message);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
