package com.example.nagoyameshi.form;

import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
//@NoArgsConstructor//引数なしコンストラクタを自動生成
@AllArgsConstructor//全フィールドを引数に持つコンストラクタを自動生成
public class TermEditForm {

    @NotBlank(message = "内容を入力してください。")
    private String content;
}
