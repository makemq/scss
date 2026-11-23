package com.example.scss.config;
import com.example.scss.annotation.RequireRole;
import org.springframework.web.method.HandlerMethod;
import com.example.scss.service.UserService;
import com.example.scss.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.scss.entity.SysUser;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class JwtInterceptor implements HandlerInterceptor {
    private final UserService userService ;
    private final JwtUtil jwtUtil;
    public JwtInterceptor(JwtUtil jwtUtil,UserService userService) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }
    private boolean unauthorized(HttpServletResponse response) throws Exception{
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);//设置HTTP 401
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        response.getWriter().write(//前端返回信息
                "{\"code\":401,\"message\":\"未登录或登录已过期\",\"data\":null}"
        );
        return false;
    }
    //spring MVC
    @Override
    public boolean preHandle(HttpServletRequest request,HttpServletResponse response,Object handler) throws Exception {
        //获取token
        String authHeader = request.getHeader("Authorization");
        //检查token
        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return unauthorized(response);
        }
        //截取token
        String  token = authHeader.substring(7);
        String username;
        String tokenType;
        try {
            username = jwtUtil.getUsernameFromToken(token);
            tokenType = jwtUtil.getTokenType(token);
        } catch (Exception e) {
            return unauthorized(response);
        }
        //允许Access Token 访问业务接口
        if (!"ACCESS".equals(tokenType)) {
            return unauthorized(response);
        }
        //判断用户名
        if(username == null||username.isBlank()){
            return unauthorized(response);
        }
        //查询判断
        SysUser user = userService.findByUsername(username);
        if (user == null) {
            return unauthorized(response);
        }
        //保存
        request.setAttribute("username",username);
        request.setAttribute("role",user.getRole());
        request.setAttribute("studentId",user.getStudentId());
        //权限判断
        //获取注解，读取角色
        if (handler instanceof HandlerMethod handlerMethod) {
            RequireRole requireRole = handlerMethod.getMethodAnnotation(RequireRole.class);
            if (requireRole != null) {
                String requiredRole = requireRole.value();//获取要求角色
                String currentRole = user.getRole();//获取当前角色
                if (!requiredRole.equals(currentRole)) {//比较角色
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(
                            "{\"code\":403,\"message\":\"没有权限访问该接口\",\"data\":null}"
                    );
                    return false;
                }
            }

        }
        return true;
    }

}

