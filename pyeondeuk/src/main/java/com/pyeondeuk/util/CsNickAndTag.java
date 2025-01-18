package com.pyeondeuk.util;

import com.pyeondeuk.controller.CsNickAndTagService;

public class CsNickAndTag {
	
	public static void main(String[] args) {
        CsNickAndTagService service = new CsNickAndTagService();
        int targetCsSeq = 1210; // 처리할 편의점의 CS_SEQ (예: 1)

        try {
            service.processReviewsForConvenienceStore(targetCsSeq);
            System.out.println("리뷰 기반 별명과 태그가 성공적으로 업데이트되었습니다.");
        } catch (Exception e) {
            System.err.println("리뷰 처리 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
