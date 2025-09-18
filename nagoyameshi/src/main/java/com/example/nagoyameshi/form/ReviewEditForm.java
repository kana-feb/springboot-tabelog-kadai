package com.example.nagoyameshi.form;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor // ← 全フィールドを引数に持つコンストラクタを自動生成
public class ReviewEditForm {
	//score
	   @NotNull(message = "評価を選択してください。")
	    @Min(value = 0, message = "座席数は0席以上に設定してください。")
	    private Integer score;
	
	//content
	   @NotBlank(message = "感想を入力してください。")
	   @Length(max = 300, message = "感想は300文字以内で入力してください。")
	   private String content;
	}	     


