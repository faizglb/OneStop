package com.acea.backend.service;

import com.acea.backend.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;

/**
 * Email service — all sends are asynchronous to avoid blocking HTTP threads.
 * Uses Thymeleaf HTML templates for professional email rendering.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender  mailSender;
    private final TemplateEngine  templateEngine;

    private static final String FROM = "ACEA Platform <noreply@acea.io>";

    // ── Public API ────────────────────────────────────────────────────────

    @Async
    public void sendVerificationEmail(User user, String verificationLink) {
        log.info("DEVELOPMENT ONLY: Verification link for [{}] is: {}", user.getEmail(), verificationLink);
        Context ctx = new Context();
        ctx.setVariable("firstName",        user.getFirstName());
        ctx.setVariable("verificationLink", verificationLink);
        ctx.setVariable("expiryHours",      24);

        sendHtmlEmail(
            user.getEmail(),
            "Verify your ACEA account",
            "email/verify-email",
            ctx
        );
        log.info("Verification email dispatched to [{}]", user.getEmail());
    }

    @Async
    public void sendPasswordResetEmail(User user, String resetLink) {
        log.info("DEVELOPMENT ONLY: Password reset link for [{}] is: {}", user.getEmail(), resetLink);
        Context ctx = new Context();
        ctx.setVariable("firstName", user.getFirstName());
        ctx.setVariable("resetLink", resetLink);
        ctx.setVariable("expiryHours", 2);

        sendHtmlEmail(
            user.getEmail(),
            "Reset your ACEA password",
            "email/reset-password",
            ctx
        );
        log.info("Password reset email dispatched to [{}]", user.getEmail());
    }

    @Async
    public void sendPasswordChangedNotification(User user) {
        Context ctx = new Context();
        ctx.setVariable("firstName", user.getFirstName());

        sendHtmlEmail(
            user.getEmail(),
            "Your ACEA password has been changed",
            "email/password-changed",
            ctx
        );
    }

    @Async
    public void sendWelcomeEmail(User user) {
        Context ctx = new Context();
        ctx.setVariable("firstName", user.getFirstName());

        sendHtmlEmail(
            user.getEmail(),
            "Welcome to ACEA — Your AI Career Advisor 🎉",
            "email/welcome",
            ctx
        );
    }

    // ── Internal ──────────────────────────────────────────────────────────

    private void sendHtmlEmail(String to, String subject, String templateName, Context ctx) {
        try {
            String html = templateEngine.process(templateName, ctx);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(FROM);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(html, true);
            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send email [{}] to [{}]: {}", subject, to, e.getMessage(), e);
            // Don't re-throw — email failure should not break the user flow
        }
    }
}
