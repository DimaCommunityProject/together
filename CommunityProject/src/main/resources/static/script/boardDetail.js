$(function() {
    // 삭제
    $("#deleteBoard").click(deleteBoard);
    $("#endBtn").click(endRecruit);
    
    // 댓글 부분으로 이동
    $("#message").click(moveToReply);
    
    // 좋아요
    $("#heart").click(heartToggle);
    
    // 신고
    $("#reportBtn").click(boardReport);
    $("#submitReportBtn").click(submitReportForm);
    
    // 모집
    $("#recruitBtn").click(boardRecruit);
    $("#submitRecruitBtn").click(submitRecruitForm);
    
    // 댓글
    $("#replySubmit").click(function(event) {
        event.preventDefault(); // 기본 폼 제출 방지
        replySubmit(); // 댓글 등록 함수 호출
    });
    $(".replyHeart").click(replyLikeToggle);
    $(".childReplyBtn").click(childReplyWrite);
    
    init();
});

// 게시글 삭제 처리 함수
function deleteBoard (){
    if(confirm("정말 삭제하시겠습니까?")){
        $("#deleteForm").submit();
    }
}

// 모집 마감 알림 함수
function endRecruit() {
    alert("인원 모집이 마감되었습니다.");
}

// 참여 처리 함수
function boardRecruit() {
    console.log("지금 참여버튼 눌렀어");
    // 참여여부 확인
    var boardId = $("#boardId").val();
    var memberId = $("#currentUser").val(); // 현재 로그인한 사용자
    $.ajax({
        url:"/board/isRecruited",
        method:"GET",
        data:{"boardId":boardId, "memberId":memberId},
        success : function (resp) {
            // resp : true이면 이미 참여한 것임.
            if(resp){ 
                alert("이미 참여한 사용자입니다.");
                // 모달을 방지하기 위해 data-bs-toggle 및 data-bs-target 속성 제거
                // $("#recruitBtn").attr("data-bs-toggle","").attr ("data-bs-target","");     
                $("#recruitBtn").removeAttr("data-bs-toggle data-bs-target");
                return;
            }else{
                // 모달창 값 채우기
                $("#modalMemberId").val($("#currentUser").val());
                $("#modalBoardId").val($("#boardId").val());
                $("#modalCategory").val($("#category").val());
                $("#modalSearchWord").val($("#searchWord").val());

                // 모달창 열기
                var recruitModal = new bootstrap.Modal(document.getElementById('recruitModal'));
                recruitModal.show();
            }       
        }
    });
}

// Recruit 폼 전송 함수
function submitRecruitForm() {
    alert("참여 완료!");
    $("#recruitForm").submit();
}

//게시글 신고 버튼
function boardReport() {
    // 모달창 값 채우기
    $("#reportModalWriter").val($("#boardWriter").val()); // 게시글 작성자 
    $("#reportModalTitle").val($("#boardTitle").val());
    $("#reportModalBoardId").val($("#boardId").val());
    $("#reportModalCategory").val($("#category").val());
    $("#reportModalSearchWord").val($("#searchWord").val());

    // 모달창 열기
    var reportModal = new bootstrap.Modal(document.getElementById('reportModal'));
    reportModal.show();
}

// Report 폼 전송 함수
function submitReportForm() {
    alert("신고 완료!");
    $("#reportForm").submit();
}

// 댓글 부분으로 이동하는 함수
function moveToReply() {
    var offset = $("#replyPart").offset(); //해당 위치 반환
    $("html, body").animate({scrollTop: offset.top},120); // 선택한 위치로 이동. 두번째 인자는 0.4초를 의미한다.
}


// 게시글 좋아요 수 반환 함수
function getLikeCount() {
    var boardId = $("#boardId").val();
    $.ajax({
        method:"GET",
        url:"/board/getLikeCount",
        data:{"boardId":boardId},
        success: function (result) {
            $("#likeCount").text(result);
        }
    });
}


// 게시글 좋아요 설정/해제 함수
function heartToggle() {
    var boardId = $("#boardId").val();
    var currentUser = $("#currentUser").val();
    $.ajax({
        method:"GET",
        url:"/board/likeUpdate",
        data:{"boardId":boardId,"memberId":currentUser},
        success:function (result) {
            if (result) {
                $("#heart").attr("class", "ti ti-heart-filled text-dark fs-6"); // 채워진 하트
            }else{
                $("#heart").attr("class", "ti ti-heart text-dark fs-6"); // 빈 하트
            }
            getLikeCount(); // 좋아요 수 업데이트
        }
    });
}

// 게시글에 대한 댓글 목록 가져오기
function getReplyList() {
    var boardId=$("#boardId").val();
    var currentUser=$("#currentUser").val();
    $.ajax({
        url:"/reply/getList",
        method:"GET",
        data:{"boardId":boardId, "memberId":currentUser},
        success:function (list) {
            $("#result").replaceWith(list);
            $(".replyHeart").click(replyLikeToggle);
            $(".childReplyBtn").click(childReplyWrite);
            $(".childReplySubmit").click(childReplySubmit);
        }
    });
}

// 게시글에 대한 댓글 수 가져오기
function getReplyCount() {
    var boardId = $("#boardId").val();
    $.ajax({
        method:"GET",
        url:"/reply/getReplyCount",
        data:{"boardId":boardId},
        success:function (result) {
            $("#replyCount").text(result);
        }
    });
}

// 댓글 등록
function replySubmit() {
    console.log("댓글 등록합니다.");
    var boardId = $("#replyBoard").val();
    var currentUser = $("#currentUser").val();
    var replyWriter = $("#replyWriter").val();
    var replyContent = $("#replyContent").val();

    $.ajax({
        method:"GET",
        url:"/reply/create",
        data:{"boardId":boardId,"memberId":replyWriter,"content":replyContent},
        success:function () {
            getReplyList(); // 댓글 목록 불러오기
            getReplyCount(); // 댓글 수 업데이트
        }
    });
}


// 댓글 좋아요
function replyLikeToggle() {
    console.log("댓글 좋아요 눌렀어");
    var target = $(this); 
    var memberId = target.attr("data-member");
    console.log("현재 로그인한 사용자 : " + memberId);
    var replyId = target.attr("data-reply");
    console.log("댓글 ID : " + replyId);

    $.ajax({
        method: "GET",
        url: "/reply/likeUpdate",
        data: { "replyId": replyId, "memberId": memberId },
        success: function(result) {
            if (result) {
                target.find('i').attr('class',"ti ti-thumb-up-filled text-dark fs-5");
            }else{
                target.find('i').attr('class',"ti ti-thumb-up text-dark fs-5");
            }

            getReplyLikeCount(target.closest('.replyHeart'), replyId); // 좋아요 수 반환
        }
    });
}

// 좋아요 수 업데이트 함수
function getReplyLikeCount(target, replyId) {
    $.ajax({
        method: "GET",
        url: "/reply/getLikeCount",
        data: { "replyId": replyId },
        success: function(likeCount) {
            target.find('span').text(likeCount); // 좋아요 수 업데이트
        }
    });
}

// 대댓글 등록 폼 생성
function childReplyWrite(){
    // 클릭된 요소의 상위 div를 찾기
    var parentDiv = $(this).closest('.childReplyForm');

    var childReplyForm = `
        <div style=" background-color: white; padding: 12px; border-radius: 2%; width: 100%; box-sizing: border-box;">
            <textarea class="form-control mb-4 childreplyContent" rows="3"></textarea> <!-- 대댓글 내용 -->
            <button class="btn bg-danger-subtle text-danger childReplySubmitCancel">취소</button> <!-- 대댓글 작성 취소 버튼 -->
            <button class="btn btn-primary ms-6 childReplySubmit">대댓글 등록</button> <!-- 대댓글 작성 버튼 -->
        </div>`;
    
    // 상위 div에 HTML 삽입
    parentDiv.html(childReplyForm);
    $(".childReplySubmitCancel").click(childReplySubmitCancel); // 대댓글 등록 취소
    $(".childReplySubmit").click(childReplySubmit); // 대댓글 등록 요청
}

// 대댓글 등록 취소
function childReplySubmitCancel(){
    // 클릭된 요소의 상위 div를 찾기
    var parentDiv = $(this).closest('.childReplyForm');

    var childReplyForm = `
        <a class="d-flex align-items-center justify-content-center text-bg-primary p-2 fs-4 rounded-circle childReplyBtn" href="javascript:void(0)" data-bs-toggle="tooltip" data-bs-placement="top" data-bs-title="Reply">
            <i class="ti ti-arrow-back-up"></i> 
        </a>`;
    
    // 상위 div에 HTML 삽입
    parentDiv.html(childReplyForm);
    $(".childReplyBtn").click(childReplyWrite); // 대댓글 등록 폼 요청
}

// 대댓글 등록 요청
function childReplySubmit() {
    // .closest() 메서드를 사용하여 가장 가까운 부모 요소 중 class가 'replyHeart'인 요소를 찾습니다.
    var replyHeartDiv = $(this).closest('.p-4').find('.replyHeart');
    
    // 'replyHeart' div에서 data 속성 값을 읽어옵니다.
    var memberId = replyHeartDiv.data("member");
    var replyId = replyHeartDiv.data("reply");
    
    console.log("댓글 작성자 ID : " + memberId);
    console.log("댓글 ID : " + replyId);

    var content = $(this).siblings('.childreplyContent').val(); // 대댓글 내용 읽기
    console.log("대댓글 내용 : " + content);

    $.ajax({
        method: "GET",
        url: "/reply/createChild",
        data: {
            "boardId": $("#boardId").val(),
            "parentReplyId": replyId,
            "memberId": memberId,
            "content": content
        },
        success: function(result) {
            if (result) {
                getReplyList(); // 댓글 목록 불러오기
                getReplyCount(); // 댓글 수 업데이트
            }
        }
    });
}

function init() {
    var category = $("#category").val();
    var boardId = $("#boardId").val();
    var currentUser = $("#currentUser").val();

    // activity/recruit 인 경우 마감 여부 확인 
    if (category=='activity' || category=='recruit') {
        $.ajax({
            method:"GET",
            url:"/board/isDead",
            data:{"boardId":boardId},
            success: function (result) {
                if (result) {
                    $("#endBtn").show();
                    $("#recruitBtn").hide();
                    $("#dDayBtn").hide();
                }else{
                    $("#endBtn").hide();
                    $("#recruitBtn").show();
                    $("#dDayBtn").show();
                }
            }
        });        
    }
    
    // 현재 로그인한 사용자의 좋아요 여부
    $.ajax({
        method:"GET",
        url:"/board/isLikeCount",
        data:{"boardId":boardId, "memberId":currentUser},
        success: function (result) {
            if (result) {
                $("#heart").attr("class", "ti ti-heart-filled text-dark fs-6"); // 채워진 하트
            }else{
                $("#heart").attr("class", "ti ti-heart text-dark fs-6"); // 빈 하트
            }
        }
    });

    // 댓글 목록 가져오기
    getReplyList();
}