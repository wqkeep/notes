package cn.itcast.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component  //自动加入到spring
public class LoginFilter extends ZuulFilter {
    @Override
    public String filterType() {
        // 登录校验，肯定是在前置拦截
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //顺序设置为PRE_DECORATION_FILTER_ORDER - 1
        return FilterConstants.PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        // 返回true，代表过滤器生效。
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        // 登录校验逻辑。
        // 获取Zuul提供的请求上下文对象
        RequestContext ctx = RequestContext.getCurrentContext();
        // 从上下文中获取request对象
        HttpServletRequest request = ctx.getRequest();
        // 获取请求参数access-token
        String token = request.getParameter("access-token");
        //判断是否存在
        if (StringUtils.isBlank(token)) {
            //不存在token，登录校验失败，则拦截
            ctx.setSendZuulResponse(false);//通过这个方法来拦截，如果判断条件不成立，默认为true放行
            //返回403状态码，也可以考虑重定向到登录页。
            ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }
        // 校验通过，可以考虑把用户信息放入上下文，继续向后执行
        return null;
    }
}
