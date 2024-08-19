package net.dima_community.CommunityProject.common.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageNavigator {		//교수님께 그대로 가져옴
	private final int pagePerGroup = 10;  
	private int pageLimit;               
	private int page;                  
	private int totalPages;               
	private int totalGroupCount;         
	private int currentGroup;            
	private int startPageGroup;            
	private int endPageGroup; 
	
	// <<  <  1 2 3 4 5 6 7 8 9 10  >  >>
	   public PageNavigator(int pageLimit, int page, int totalPages) {
	      // 멤버 초기화
	      this.pageLimit = pageLimit;
	      this.page = page;
	      this.totalPages = totalPages;
	      
	      // 총 그룹수 계산
	      // 10페이지 == >그룹이 1개, 11페이지 ==> 그룹이 2개
	      totalGroupCount = totalPages / pagePerGroup; 
	      totalGroupCount += (totalPages % pagePerGroup == 0) ? 0 : 1;
	      
	      // 사용자가 요청한 페이지의 첫번째 글번호와 마지막 글번호 계산
	      startPageGroup = ((int)(Math.ceil((double)page / pageLimit) -1) * pageLimit + 1);
	       
	      //
	      endPageGroup = (startPageGroup + pageLimit - 1) < totalPages 
	            ? (startPageGroup + pageLimit - 1) 
	            : totalPages;
	      
	      // 검색과 함께 사용했는데 검색 결과가 하나도 없으면
	      // startPageGroup = 1 이고 endPageGroup=0이 되므로 이런 경우 endPageGroup=1로 한다
	      if(endPageGroup == 0) endPageGroup = 1;
	      
	      // 요청한 페이지가 속한 그룹 계산 
	      // (5-1)/10 +1 ==> 1그룹, (11-1)/10 + 1 ==> 2그룹
	      currentGroup = (page - 1) / pagePerGroup + 1 ;
	       
	   }
}
