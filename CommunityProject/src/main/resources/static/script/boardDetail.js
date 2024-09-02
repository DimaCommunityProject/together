$(function() {
    $("#deleteBoard").click(deleteBoard);
    $("#endBtn").click(endRecruit);
    $("#recruitBtn").click(boardRecruit);
    $("#submitRecruitBtn").click(submitRecruitForm);
    $("#reportBtn").click(boardReport);
    $("#submitReportBtn").click(submitReportForm);
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
    // 참여여부 확인
    var boardId = $("#boardId").val();
    var memberId = $("#memberId").val();
    $.ajax({
        url:"/board/isRecruited",
        method:"GET",
        data:{"boardId":boardId, "memberId":memberId},
        success : function (resp) {
        // resp : true이면 이미 참여한 것임.
        if(resp){ 
            alert("이미 참여한 사용자입니다.");
        }else{
            // 모달창 값 채우기
            $("#modalMemberId").val($("#currentUser").val());
            $("#modalBoardId").val($("#boardId").val());
            $("#modalCategory").val($("#category").val());
            $("#modalSearchWord").val($("#searchWord").val());
            // document.getElementById("modalMemberId").value = document.getElementById("currentUser").value;
            // document.getElementById("modalBoardId").value = document.getElementById("boardId").value;
            // document.getElementById("modalCategory").value = document.getElementById("category").value;
            // document.getElementById("modalSearchWord").value = document.getElementById("searchWord").value;
            
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
    $("#reportModalWriter").val($("#boardWriter").val());
    $("#reportModalTitle").val($("#boardTitle").val());
    $("#reportModalBoardId").val($("#boardId").val());
    $("#reportModalCategory").val($("#category").val());
    $("#reportModalSearchWord").val($("#searchWord").val());

    // 모달창 열기
    var recruitModal = new bootstrap.Modal(document.getElementById('reportModal'));
    recruitModal.show();
}

// 게시글 신고 버튼
// function boardReport() {
//     // 모달창 값 채우기
//     document.getElementById("reportModalWriter").value = document.getElementById("boardWriter").value;
//     document.getElementById("reportModalTitle").value = document.getElementById("boardTitle").value;
//     document.getElementById("reportModalBoardId").value = document.getElementById("boardId").value;
//     document.getElementById("reportModalCategory").value = document.getElementById("category").value;
//     document.getElementById("reportModalSearchWord").value = document.getElementById("searchWord").value;

//     // 모달창 열기
//     var recruitModal = new bootstrap.Modal(document.getElementById('reportModal'));
//     recruitModal.show();
// }


// Report 폼 전송 함수
function submitReportForm() {
    alert("신고 완료!");
    $("#reportForm").submit();
}



function init() {
    var category = $("#category").val();
    var boardId = $("#boardId").val();

    if (category=='activity'||category=='recruit') {
        // activity/recruit 인 경우 마감 여부 확인 - deadline이 null인지 아닌지
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

}