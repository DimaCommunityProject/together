$(function(){
    $("#submitBtn").click(submitBoardWrite);
    $("#resetBtn").click(resetBoardWrite);
    $("#cancelBtn").click(cancelBoardWrite);
    init();
});

// 게시글 등록 처리 요청
function submitBoardWrite(){
    // 유효성 검사
    if(!validTitle()) return;
    else if(!validContent()) return;

    alert("게시글 등록!");
    $("#writeForm").submit();
}

// 유효성 검사 - title
function validTitle(){
    var title = $("#inputText2").val().trim();
    if (!title) {
        alert("제목을 입력하세요");
        // $("#validTitle").text("제목을 입력하세요")
        $("#inputText2").select();
        return false;
    }
    return true;
}

// 유효성 검사 - content
function validContent(){
    // ckeditor에서 글 내용 가져오기
    var content = window.editor.getData();
    console.log("content : "+content);
    if (!content) {
        // alert("내용을 입력하세요");
        $(".ck-content").focus(); // ckeditor에 focus
        return false;
    }else{
        $("#content").val(content); // content에 값 채워넣기
        return true;
    }
}

// 게시글 등록 초기화 (제목, 내용, 파일)
function resetBoardWrite(){
    var category = $("#inputText1").val();
    resetTitle();
    resetCkeditor();
    resetUploadFile();
    if (category == "recruit" || category =="activity") {
        resetDeadline();
        resetLimitNumber();
    }
}

// Title 값 삭제
function resetTitle(){
    $("#inputText2").val('');
}
// ckeditor 값 삭제
function resetCkeditor(){
    window.editor.setData('');
}
// uploadFile 삭제
function resetUploadFile(){
    $("#inputGroupFile01").val('');
}
// deadline 초기화
function resetDeadline(){
    $("#deadline").val('');
}
// limitNumber 초기화
function resetLimitNumber(){
    $("#limitNumber").val('');
}

// 게시글 등록 취소 (게시글 목록 요청)
function cancelBoardWrite(){
    $("#backToList").submit();
}

function init(){
    getMemberGroup();
}

function getMemberGroup() {
    // memberGroup 가져오기
    var memberId = $("#memberId").val();
    $.ajax({
    url:"/member/getMemberGroup",
    data:{"memberId":memberId},
    method:"GET",
    success : function (result) {
        console.log( "memberGroup : " + result);
        $("#memberGroup").val(result);
        $("#userGroup").val(result);
    }
    });
}
