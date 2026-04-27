package com.asset.demo.config;

import com.asset.demo.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    private static final String ADMIN = "ADMIN";
    private static final String MANAGER = "MANAGER";
    private static final String EMPLOYEE = "EMPLOYEE";

    private final UserService userService;
    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain SecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests((authorize) -> authorize

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/auth/login").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/auth/account-status").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/auth/user-status").authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/auth/user-status/v2").authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/asset/get-all")
                        .hasAnyAuthority(ADMIN, EMPLOYEE)

                        .requestMatchers(HttpMethod.POST, "/api/employee/add").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/employee/get-all")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/employee/get/{employeeId}")
                        .hasAnyAuthority(ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/employee/filter")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.POST, "/api/admin/get-all")
                        .hasAnyAuthority(ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/manager/add").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/manager/get-all")
                        .hasAuthority(ADMIN)

                        .requestMatchers(HttpMethod.GET, "/api/manager/get/{managerId}")
                        .hasAuthority(ADMIN)

                        .requestMatchers(HttpMethod.PUT, "/api/manager/approve-employee/{employeeId}")
                        .hasAuthority(MANAGER)

                        .requestMatchers(HttpMethod.PUT, "/api/manager/get-all/status/{status}")
                        .hasAuthority(ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/manager-document/upload")
                        .hasAuthority(MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/manager-document/profile")
                        .hasAuthority(MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/admin/add")
                        .hasAuthority(ADMIN)

                        .requestMatchers(HttpMethod.PUT, "/api/admin/approve-manager/{managerId}")
                        .hasAuthority(ADMIN)

                        .requestMatchers(HttpMethod.POST, "/api/asset-category/add")
                        .hasAnyAuthority(ADMIN)

                        .requestMatchers(HttpMethod.GET, "/api/admin/get-one")
                        .hasAnyAuthority(ADMIN)

                        .requestMatchers(HttpMethod.GET, "/api/asset-category/get-all")
                        .hasAnyAuthority(ADMIN, EMPLOYEE)

                        .requestMatchers(HttpMethod.GET, "/api/asset-category/get/{categoryId}")
                        .hasAnyAuthority(ADMIN, EMPLOYEE)

                        .requestMatchers(HttpMethod.POST, "/api/asset/add")
                        .hasAnyAuthority(ADMIN)

                        .requestMatchers(HttpMethod.GET, "/api/asset/get-all")
                        .hasAnyAuthority(ADMIN, EMPLOYEE, MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/asset/get/{assetId}")
                        .hasAnyAuthority(ADMIN, EMPLOYEE, MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/asset/get/category/{categoryId}")
                        .hasAnyAuthority(ADMIN, EMPLOYEE, MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/asset/get-all/v1")
                        .hasAnyAuthority(ADMIN, EMPLOYEE, MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/asset/asset-status")
                        .hasAnyAuthority(ADMIN, EMPLOYEE, MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/asset-request/get/user")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.POST, "/api/asset-request/add/{assetId}")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.GET, "/api/asset-request/get-all")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/asset-request/get/status/{status}")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.DELETE, "/api/asset-request/delete/{assetRequestId}")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.POST, "/api/asset-request/add/{assetId}/{employeeId}/v1")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.GET, "/api/asset-request/request-status")
                        .authenticated()

                        .requestMatchers(HttpMethod.PUT, "/api/asset-allocation/allocate/{assetRequestId}")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.PUT, "/api/asset-allocation/reject/{assetRequestId}")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.PUT, "/api/asset-allocation/return-asset-request/{assetAllocationId}")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.GET, "/api/asset-allocation/get-all/allocated")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.GET, "/api/asset-allocation/get-all")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/asset-allocation/accept-return-request/{assetAllocationId}")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/asset-allocation/get/status/{status}")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/asset-allocation/get/user/status/{status}")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.PUT, "/api/asset-allocation/cancel-return-asset-request/{assetAllocationId}")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.GET, "/api/asset-allocation/allocation-status")
                        .authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/service-request/request-service/{assetAllocationId}")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.PUT, "/api/service-request/accept-service-request/{serviceRequestId}")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.PUT, "/api/service-request/reject-service-request/{serviceRequestId}")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.PUT, "/api/service-request/resolved-service-request/{serviceRequestId}")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/service-request/user/get-all")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.GET, "/api/service-request/get-all")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/service-request/status/{status}")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.DELETE, "/api/service-request/delete/{serviceRequestId}")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.GET, "/api/service-request/user/status/{status}")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.GET, "/api/service-request/service-status")
                        .hasAnyAuthority(EMPLOYEE)

                        .requestMatchers(HttpMethod.POST, "/api/asset-audit/audit/{assetAuditResultId}")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.POST, "/api/asset-audit/create")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/asset-audit/get-all-audit-dates")
                        .hasAnyAuthority(MANAGER)

                        .requestMatchers(HttpMethod.GET, "/api/asset-audit/audit-status")
                        .hasAnyAuthority(MANAGER)

                        .anyRequest().permitAll()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(Customizer.withDefaults());

        return http.build();
    }
}