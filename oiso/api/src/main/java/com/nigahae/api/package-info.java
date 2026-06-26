//예시 @ApplicationModule(allowedDependencies = {"user", "order", "product", "common"})
@ApplicationModule(allowedDependencies = {"common", "user"})
package com.nigahae.api;

import org.springframework.modulith.ApplicationModule;