package com.tuencargo.infrastructure.web;

import com.tuencargo.application.ports.in.NotificationUseCase;
import com.tuencargo.infrastructure.security.AuthenticatedUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationController {

    private final NotificationUseCase notifications;

    public NotificationController(NotificationUseCase notifications) {
        this.notifications = notifications;
    }

    @GetMapping("/notifications")
    public String list(Model model, @AuthenticationPrincipal AuthenticatedUser user) {
        model.addAttribute("notifications", notifications.listByUser(user.id()));
        return "notifications/list";
    }
}
