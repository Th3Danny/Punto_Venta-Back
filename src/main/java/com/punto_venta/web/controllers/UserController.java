package com.punto_venta.web.controllers;

import com.punto_venta.services.IUserService;
import com.punto_venta.web.dtos.request.CreateUserRequest;
import com.punto_venta.web.dtos.request.UpdateUserRequest;
import com.punto_venta.web.dtos.response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    private final IUserService userService;

    public UserController (IUserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<BaseResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        BaseResponse baseResponse = userService.createUser(request);
        return baseResponse.buildResponseEntity();
    }

    @GetMapping("/{id}")
    public  ResponseEntity<BaseResponse> getUserById (@PathVariable Long id) {
        BaseResponse baseResponse = userService.getUserById(id);
        return baseResponse.buildResponseEntity();
    }

    @GetMapping
    public ResponseEntity<BaseResponse> getAllUsers (){
        BaseResponse baseResponse = userService.getAllUser();
        return baseResponse.buildResponseEntity();
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<BaseResponse> getUserByEmail (@PathVariable String email){
        BaseResponse baseResponse = userService.getUserByEmail(email);
        return baseResponse.buildResponseEntity();
    }

    @PutMapping("/{id}")
    public ResponseEntity<BaseResponse> updateUser (@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request){
        BaseResponse baseResponse = userService.updateUser(id, request);
        return baseResponse.buildResponseEntity();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<BaseResponse> deleteUser (@PathVariable Long id){
        BaseResponse baseResponse = userService.deleteUser(id);
        return baseResponse.buildResponseEntity();
    }

    @GetMapping("/roles")
    public ResponseEntity<BaseResponse> getRoles() {
        BaseResponse baseResponse = userService.getAllRoles();
        return baseResponse.buildResponseEntity();
    }
}