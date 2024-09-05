$(function(){
    $("#submitBtn").click(submitBoardUpdate);
    $("#resetBtn").click(resetBoardUpdate);
    $("#cancelBtn").click(cancelBoardUpdate);
    $("#fileDeleteBtn").click(deleteFile);
    init();
});

// 게시글 수정 처리 요청
function submitBoardUpdate(){
    // 유효성 검사
    if(!validTitle()) return;
    else if(!validContent()) return;
    else if(!validLimitNumber()) return;

    if ($("#inputGroupFile01")[0].files.length>0) {
        console.log("파일 존재 ");
        console.log(($("#inputGroupFile01")[0]));
    }else{
        console.log("파일 존재하지 않음");
    }

    alert("게시글 수정!");
    $("#updateForm").submit();
}

// 유효성 검사 - title
function validTitle(){
    var title = $("#inputText2").val().trim();
    if (!title) {
        alert("제목을 입력하세요");
        // $("#validTitle").text("제목을 입력하세요")
        $("#inputText2").select();
        return false;
    }else return true;
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

// 유효성 검사 - limitNumber
function validLimitNumber() {
    var limitNumber = $("#limitNumber").val(); // limitNumber 가져오기
    var currentNumber = $("#currentNumber").val(); // currentNumber 가져오기
    if (limitNumber<currentNumber) {
        alert("모집 예정 인원수가 현재 모집된 수("+currentNumber+ "명)보다 많아야 합니다.");
        return false;
    }else return true;
}

// 게시글 등록 초기화 (제목, 내용, 파일)
function resetBoardUpdate(){
    window.location.reload(); // 새로고침
}

// 게시글 수정 취소 (게시글 목록 요청)
function cancelBoardUpdate(){
    console.log("취소 버튼 눌렀어");
    console.log($("#backToDetail"));
    $("#backToDetail").submit(); // board/detail 요청 
}

// 파일 삭제버튼 클릭 시 -> 파일 선택하는 태그 집어 넣기
function deleteFile() {
    var selectFileTag =
    `<div class="input-group">
        <span class="input-group-text">Upload</span>
        <div class="custom-file">
            <input type="file" name="uploadFile" class="form-control" id="inputGroupFile01"/>
        </div>
    </div>`;
    $("#filePart").html(selectFileTag);
    $("#deleteOriginalFile").val("yes");
}


function init(){
    setCkEditor();
    getMemberGroup();
}

// CkEditor 기존 내용으로 세팅하는 함수
function setCkEditor(){
    var content = $("#boardContent").val();
    editor.setData(content);
}

// memberGroup 가져오는 함수
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
    }
    });
}
