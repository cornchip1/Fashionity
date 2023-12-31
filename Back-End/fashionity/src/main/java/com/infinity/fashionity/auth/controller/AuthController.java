package com.infinity.fashionity.auth.controller;

import com.infinity.fashionity.auth.dto.*;
import com.infinity.fashionity.auth.service.AuthService;
import com.infinity.fashionity.security.dto.JwtAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/login",produces = APPLICATION_JSON_VALUE,consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginDTO.Response> login(
            @RequestBody LoginDTO.Request dto
    ) {
        LoginDTO.Response response = authService.login(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(value = "/register",produces = APPLICATION_JSON_VALUE,consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<SaveDTO.Response> register(
            @RequestBody @Valid SaveDTO.Request dto
    ) {
        SaveDTO.Response response = authService.register(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/check/id")
    public ResponseEntity<Boolean> isIdValidate(
            @RequestParam String id
    ) {
        boolean response = authService.isIdValidate(id);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/check/nickname")
    public ResponseEntity<Boolean> isNicknameValidate(
            @RequestParam String nickname
    ) {
        boolean response = authService.isNicknameValidate(nickname);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @GetMapping("/check/email")
    public ResponseEntity<Boolean> isEmailValidate(
            @RequestParam String email
    ) {
        boolean response = authService.isEmailValidate(email);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PostMapping("/find/id")
    public ResponseEntity<FindByEmailDTO.IDResponse> findIdByEmail(
            @RequestBody FindByEmailDTO.IDRequest dto
    ) {
        FindByEmailDTO.IDResponse response = authService.findIdByEmail(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/find/password")
    public ResponseEntity<FindByEmailDTO.PasswordResponse> findPasswordByEmail(
            @RequestBody FindByEmailDTO.PasswordRequest dto
    ) {
        FindByEmailDTO.PasswordResponse response = authService.reissuePasswordByEmail(dto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutDTO.Response> logout (
            @AuthenticationPrincipal JwtAuthentication auth
    ) {
        LogoutDTO.Response response = authService.logout();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reissue")
    public ResponseEntity<ReissueDTO.Response> reissue(
            @RequestBody ReissueDTO.Request dto,
            @CookieValue(name = "refreshToken") String refreshToken
    ) {
        ReissueDTO.Response response = authService.reissue(refreshToken, dto.getAccessToken(), dto.getMemberSeq());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
