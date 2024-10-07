$(function () {
    $("#find_id").on('click', findId_click);
})

function findId_click() {
    let memberName = $('#memberName').val()
    let memberEmail = $('#memberEmail').val()

    // 이전 경고 메시지 지우기
    $("#confirmEmail").html("");
    $("#confirmName").html("");
    $("#warnName").hide();
    $("#warnEmail").hide();

    if (memberName.trim().length == 0) {
        $("#confirmName").css("color", "red");
        $("#confirmName").html("이름을 입력해 주세요.");
        $("#warnName").show();
        return;
    }
    //이메일 유효성 검사
    if (memberEmail.trim().length == 0) {
        $("#confirmEmail").css("color", "red");
        $("#confirmEmail").html("이메일을 입력해 주세요.");
        $("#warnEmail").show();
        return;
    }
    let emailPattern = /^([\w-]+(?:\.[\w-]+)*)@((?:[\w-]+\.)*\w[\w-]{0,66})\.([a-z]{2,6}(?:\.[a-z]{2})?)$/i;
    if (!emailPattern.test(memberEmail)) {
        $("#confirmEmail").css("color", "red");
        $('#confirmEmail').html("이메일 형식을 다시 확인하세요.");
        $("#warnEmail").show();
        return;
    }
    // 유효성 검사를 통과한 경우, 폼을 제출
    // $("#idForm").submit();

    $.ajax({
        url: "/member/findIdResult"
        , method: "POST"
        , data: { "memberName": memberName, "memberEmail": memberEmail }
        , success: function (data) {
            console.log(data);
            if (data === "") {
                document.getElementById('modalMessage2').textContent = "조회결과가 없습니다.";
                const myModal = new bootstrap.Modal(document.getElementById('al-danger-alert'));
                myModal.show();

                // 모달의 continue 버튼 클릭 시 사용자가 쓴 거 지움
                document.getElementById('conbtn2').addEventListener('click', function () {
                    $("#memberName").val("");
                    $("#memberEmail").val("");
                });
            } else {
                document.getElementById('modalMessage').textContent = "회원님의 아이디는 " + data + "입니다.";
                const myModal = new bootstrap.Modal(document.getElementById('al-success-alert'));
                myModal.show();

                // 모달의 continue 버튼 클릭 시 사용자가 쓴 거 지움
                document.getElementById('conbtn').addEventListener('click', function () {
                    $("#memberName").val("");
                    $("#memberEmail").val("");
                });
            }
        },//end function data
        error: function () {
            document.getElementById('modalMessage2').textContent = "서버와의 통신 중 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.";
            const myModal = new bootstrap.Modal(document.getElementById('al-danger-alert'));
            myModal.show();

            // 모달의 continue 버튼 클릭 시 사용자가 쓴 거 지움
            document.getElementById('conbtn2').addEventListener('click', function () {
                $("#memberName").val("");
                $("#memberEmail").val("");
            });
        }
    });

};//end findId_click