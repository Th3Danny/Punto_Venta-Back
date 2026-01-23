package com.punto_venta.web.dtos.response;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Builder
@Data
public class BaseResponse {
    private Object data;
    private String message;
    private Boolean success;
    private HttpStatus httpStatus;

    public static class Builder {
        private Object data;
        private String message;
        private Boolean success;
        private HttpStatus httpStatus;


        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder success(Boolean success) {
            this.success = success;
            return this;
        }

        public Builder httpStatus(HttpStatus status) {
            this.httpStatus = status;
            return this;
        }

        public BaseResponse build() {
            return new BaseResponse(data, message, success, httpStatus);
        }
    }

    public ResponseEntity<BaseResponse> buildResponseEntity() {
        return ResponseEntity.status(httpStatus != null ? httpStatus : HttpStatus.OK).body(this);
    }

    public static Builder builder() {
        return new Builder();
    }

    public BaseResponse(Object data, String message, Boolean success, HttpStatus httpStatus) {
        this.data = data;
        this.message = message;
        this.success = success;
        this.httpStatus = httpStatus;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}

