package com.example.nagoyameshi.service;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.example.nagoyameshi.entity.Term;
import com.example.nagoyameshi.form.TermEditForm;
import com.example.nagoyameshi.repository.TermRepository;

@Service
public class TermService {
	   private final TermRepository termRepository;
	   // 依存性の注入（DI）を行う（コンストラクタインジェクション）
	   public TermService(TermRepository termRepository) {
	       this.termRepository = termRepository;
	   }

	   // idが最も大きい利用規約を取得する
	   public Term findFirstTermByOrderByIdDesc() {
	       return termRepository.findFirstByOrderByIdDesc();
	   }

	   //フォームから送信された利用規約の内容でデータベースを更新する。
	   @Transactional
	   public void updateTerm(TermEditForm termEditForm, Term term) {
	       term.setContent(termEditForm.getContent());

	       termRepository.save(term);
	   }
	}