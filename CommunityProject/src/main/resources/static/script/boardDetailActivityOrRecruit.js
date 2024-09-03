$(function() {
    $("#deleteBoard").click(deleteBoard);
    $("#endBtn").click(endRecruit);
    
    $("#recruitBtn").click(boardRecruit());
    $("#submitRecruitBtn").click(submitRecruitForm);
    
    $("#reportBtn").click(boardReport);
    $("#submitReportBtn").click(submitReportForm);
    
    $("#message").click(moveToReply);
    $("#heart").click(heartToggle);

    $("#replySubmit").click(replySubmit);
    $(".childReplyBtn").click(childReplyWrite);
    $(".replyHeart").click(replyLikeToggle);
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
    $("html, body").animate({scrollTop: offset.top},150); // 선택한 위치로 이동. 두번째 인자는 0.4초를 의미한다.
}


// 좋아요 수 반환 함수
function getLikeCount() {
    var boardId = $("#boardId").val();
    $.ajax({
        method:"GET",
        url:"/board/getLikeCount",
        data:{"boardId":boardId},
        success: function (result) {
            console.log("== 갱신된 좋아요 개수 : "+ result);
            $("#likeCount").text(result);
        }
    });
}


// 좋아요 설정/해제 함수
function heartToggle() {
    var boardId = $("#boardId").val();
    var currentUser = $("#currentUser").val();
    $.ajax({
        method:"GET",
        url:"/board/likeUpdate",
        data:{"boardId":boardId,"memberId":currentUser},
        success:function (result) {
            if (result) {
                $("#heart").attr("class", "ti ti-hearts text-dark fs-6"); // 채워진 하트
            }else{
                $("#heart").attr("class", "ti ti-heart text-dark fs-6"); // 빈 하트
            }
            getLikeCount(); // 좋아요 수 업데이트
        }
    });
}

// 댓글 등록
function replySubmit() {
    console.log("댓글 등록합니다.");
    console.log($("#boardId"));
    var boardId = $("#boardId").val();
    console.log("boardId : "+boardId);
    var currentUser = $("#currentUser").val();
    var replyWriter = $("#replyWriter").val();
    var replyContent = $("#replyContent").val();
    // $.ajax({
    //     method:"GET",
    //     url:"/reply/create",
    //     data:{"boardId":boardId,"memberId":replyWriter,"content":replyContent},
    //     success:function () {
    //         console.log("댓글 등록 성공");
    //         $.ajax({
    //             url:"/reply/getList",
    //             method:"GET",
    //             data:{"boardId":boardId, "memberId":currentUser},
    //             success:function (list) {
    //                 $("#result").replaceWith(list);
    //             }
    //         });
    //     }
    // });
}

// 댓글 좋아요
function replyLikeToggle() {
    var target = $(this);
    var memberId = $(this).data("member");
    console.log("현재 로그인한 사용자 : "+memberId);
    var replyId = $(this).data("reply");
    console.log("댓글 ID : "+reply);

    $.ajax({
        method:"GET",
        url:"/reply/likeUpdate",
        data:{"replyId":replyId, "memberId":memberId},
        success:function (result) {
            if (result) {
                target.attr("class", "ti ti-hearts text-dark fs-6 replyHeart")
            }else{
                target.attr("class", "ti ti-heart text-dark fs-6 replyHeart")
            }
            getReplyLikeCount(target,replyId); // 좋아요 수 반환
        }
    });
}

function getReplyLikeCount(target,replyId) {
    $.ajax({
        method:"GET",
        url:"/reply/getLikeCount",
        data:{"replyId":replyId},
        success:function (result) {
            target.text(result);
        }
    });
}

// 대댓글 등록 폼 생성
function childReplyWrite(){
    console.log( $(this));
}


function init() {
    var category = $("#category").val();
    var boardId = $("#boardId").val();
    var currentUser = $("#currentUser").val();

    // activity/recruit 인 경우 마감 여부 확인 
    if (category=='activity'||category=='recruit') {
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
                $("#heart").attr("class", "ti ti-hearts text-dark fs-6"); // 채워진 하트
            }else{
                $("#heart").attr("class", "ti ti-heart text-dark fs-6"); // 빈 하트
            }
        }
    });

    // 댓글 목록 가져오기
    $.ajax({
        url:"/reply/getList",
        method:"GET",
        data:{"boardId":boardId, "memberId":currentUser},
        success:function (list) {
            $("#result").replaceWith(list);
        }
    });

}