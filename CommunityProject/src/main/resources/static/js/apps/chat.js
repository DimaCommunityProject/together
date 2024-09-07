$(document).ready(function () {
    let stompClient = null;
    let currentChatRoomId = null;
    let currnetChatUniqueKey = null;
    let currentUserId = $('.userId').text().trim();
    let currentUserName = $('.userName').text().trim();
    let currentUniqueKey = null; 
    var chatarea = $("#chat");

    // 초기 데이터 로드
    function loadInitialData() {
        $.ajax({
            url: '/api/chat/chatData',
            method: 'GET',
            success: handleInitialData,
            error: function (error) {
                console.error('Error loading initial data:', error);
            }
        });
    }

 	// 초기 데이터를 처리하는 함수
    function handleInitialData(response) {
        console.log("Received data:", response);

        // 사용자 정보 설정
        updateUserInfo(response.currentUser);

        // 채팅방 목록 로드
        updateChatRooms(response.chatRooms);

        // 대화 상대 목록 로드
        updateRecipientSelect(response.members);
    }

 	// 사용자 정보를 업데이트하는 함수
    function updateUserInfo(currentUser) {
        if (currentUser && currentUser.memberId) {
            currentUserId = currentUser.memberId.trim();
            currentUserName = currentUser.memberName;
			
            $('.userName').text(currentUser.memberName);
            $('.userId').text(currentUser.memberId);
            $('.userRole').text(currentUser.memberRole);
            $('.userEmail').text(currentUser.memberEmail);
            $('.userProfileImage').attr('src', `../../images/profile/${currentUser.memberName}.jpg`);
        } else {
            console.error("Invalid user data.");
        }
    }


    // 채팅방 목록 업데이트
    function updateChatRooms(chatRooms) {
		
		// 유니크 값으로 멤버 이름 조회 
		const uniqueKey=chatRooms.uniqueKey;
		console.log("uniqueKey", uniqueKey);

        $('#chatList').empty();
        chatRooms.forEach(renderChatRoom);
    }
    
 	// 채팅방 UI 렌더링
    function renderChatRoom(room) {
        let imagesHtml = '';
        let memberNames = '';
        let uniqueKey = room.uniqueKey; // uniqueKey 가져오기

        if (Array.isArray(room.members)) {
            room.members.forEach(function (member, index) {
                if (member.deleted === 0) {
                    imagesHtml += generateMemberImageHtml(member);
                    memberNames += member.memberName + (index < room.members.length - 1 ? ', ' : '');
                }
            });
        } else {
            console.error('Members is not defined or not an array for room: ' + room.id);
        }

        const roomHtml = `
            <li>
                <a href="javascript:void(0)" class="px-4 py-3 bg-hover-light-black d-flex align-items-start justify-content-between chat-user bg-light-subtle" 
                	data-room-id="${room.id}" 
                	data-unique-key="${uniqueKey}">
                    <div class="d-flex align-items-center">
                        ${imagesHtml} 
                        <div class="ms-3 d-inline-block w-75">
                            <h6 class="mb-1 fw-semibold chat-title" data-username="${memberNames}">
                                ${memberNames}
                            </h6>
                        </div>
                    </div>
                </a>
            </li>
        `;
        
        $('#chatList').append(roomHtml);
        registerChatRoomClickEvent(); // 클릭 이벤트 등록
    }
    
    // 채팅방 멤버 UI 업데이트
    function updateChatPartnerUI(members) {
	    let imagesHtml = '';
	    let namesHtml = '';
	
	    if (Array.isArray(members)) {
	        members.forEach(function (member) {
	            const memberName = member.name || 'Unknown'; // 기본값 설정
	            imagesHtml += `<img src="../../images/profile/${memberName}.jpg" alt="${memberName}" width="48" height="48" class="rounded-circle ms-1" />`
	            namesHtml += `${memberName}, `;
	        });
	
	        namesHtml = namesHtml.slice(0, -2); // 마지막 쉼표 제거
	    } else {
	        console.error('Members is not defined or not an array');
	    }
	
	    $('#chatPartnerImages').html(imagesHtml);
	    $('#chatPartnerName').text(namesHtml);
	}


    // 멤버 이미지 HTML 생성
    function generateMemberImageHtml(member) {
	    const memberName = member.memberName || 'default'; // undefined 방지
	    const badgeClass = member.isOnline ? 'bg-success' : 'bg-light';   // 상태에 따라 변경 가능
	    return `
	        <span class="position-relative">
	            <img src="../../images/profile/${memberName}.jpg" alt="${memberName}" width="48" height="48" class="rounded-circle ms-1" />
	            <span class="position-absolute bottom-0 end-0 p-1 badge rounded-pill ${badgeClass}">
	                <span class="visually-hidden">New alerts</span>
	            </span>
	        </span>
	    `;
	}



    // 대화 상대 목록 업데이트
    function updateRecipientSelect(members) {
        $('#recipientSelect').empty();
        members.forEach(function (member) {
            if (member.memberId !== currentUserId) {
                $('#recipientSelect').append(`<option value="${member.memberId}">${member.memberName}</option>`);
            }
        });
    }

 	// 채팅방 클릭 이벤트 등록
    function registerChatRoomClickEvent() {
        $('.chat-user').off('click').on('click', function () {
            const selectedRoomId = $(this).data('room-id');
            if (selectedRoomId) {
                $('.chat-not-selected').addClass('d-none');
                joinChatRoom(selectedRoomId);
            }
        });
    }
    

 	// STOMP 연결 및 구독 설정
    function connect() {
        const socket = new SockJS('/stomp/chat');
        stompClient = Stomp.over(socket);

        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
            if (currentChatRoomId) {
                joinChatRoom(currentChatRoomId);
            }
        }, function (error) {
            console.log("WebSocket connection closed:", error);
        });
    }
 	
    // 메시지 처리
    function showMessage(message) {
    	console.log(message);
    	const senderId = message.senderId.trim();  // 메시지를 보낸 사용자의 ID를 가져옴
	    const messageHtml = senderId === currentUserId
	        ? generateSentMessageHtml(message) // 자신이 보낸 메시지
	        : generateReceivedMessageHtml(message); // 다른 사용자가 보낸 메시지
	
	    // messageArea가 선택된 상태에서 메시지를 추가
	    $('#messageArea').append(messageHtml);
	    scrollToBottom(); // 메시지를 추가한 후 스크롤을 최하단으로 이동
	}
    
    function generateSentMessageHtml(message) {
        const messageDate = new Date(message.timestamp);
        const hours = messageDate.getHours().toString().padStart(2, '0');
        const minutes = messageDate.getMinutes().toString().padStart(2, '0');
        const timeString = `${hours}:${minutes}`;
        return  `
	        <div class="hstack gap-3 align-items-start mb-7 justify-content-end">
		        <div class="text-end">
		            <div class="p-2 bg-info-subtle text-dark rounded-1 d-inline-block fs-3">
		                ${message.content}
		            </div>
		            <div class="text-muted fs-2">${timeString}</div>
		        </div>
		    </div>
	    `;
    }

    function generateReceivedMessageHtml(message) {
        const messageDate = new Date(message.timestamp);
        const hours = messageDate.getHours().toString().padStart(2, '0');
        const minutes = messageDate.getMinutes().toString().padStart(2, '0');
        const timeString = `${hours}:${minutes}`;
        return `
	        <div class="hstack gap-3 align-items-start mb-7 justify-content-start">
		        <img src="../../images/profile/${encodeURIComponent(message.senderName)}.jpg" alt="${message.senderName}" width="40" height="40" class="rounded-circle userProfileImage" />
		        <div>
		            <h6 class="fs-2 text-muted">${message.senderName}</h6>
		            <div class="d-flex align-items-center">
			            <div class="p-2 text-bg-light rounded-1 d-inline-block text-dark fs-3">
			                ${message.content}
			            </div>
			            <div class="text-muted ms-2 fs-2">${timeString}</div>
		            </div>
		        </div>
		    </div>
	    `;
    }
    
   function leaveMessageHtml(message){
	   leaveMessageHtml = `
           <div class="hstack gap-3 align-items-start mb-7 justify-content-start">
               <div class="p-2 text-danger d-inline-block text-dark fs-3">
                   ${message.content}
               </div>
           </div>`;
   }

    // 스크롤을 최하단으로 이동시키는 함수
    function scrollToBottom() {
        const chatBox = document.querySelector('.chat-box-inner');
        chatBox.scrollTop = chatBox.scrollHeight;
    }
    
    $('#startChat').click(function () {
        const recipientId = $('#recipientSelect').val();  // 선택한 상대방의 ID
        if (recipientId) {
            axios.post('/api/chat/start', `recipientId=${encodeURIComponent(recipientId)}`, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
            .then(response => {
                const chatRoomId = response.data.id; // 서버에서 반환된 채팅방 ID
                if (chatRoomId) {
                    joinChatRoom(chatRoomId); // 해당 채팅방으로 연결
                    loadInitialData();  // 전체 초기 데이터 로드, 채팅방 목록 갱신 포함

                    // 상대방에게 새로운 채팅방 정보를 알림
                    stompClient.send(`/user/${recipientId}/queue/newChatRoom`, {}, JSON.stringify({ roomId: chatRoomId }));
                } else {
                    console.error('Failed to retrieve chat room ID.');
                }
            })
            .catch(error => {
                console.error('Error starting chat room:', error);
            });
        } else {
            console.error('Recipient ID is not selected.');
        }
    });

    $('#sendButton').click(function () {
        const content = $('#messageInput').val();
        if (content && stompClient && currentChatRoomId) {
            const message = {
                chatRoomId: currentChatRoomId,
                senderId: currentUserId,
                senderName: currentUserName,
                content: content,
                timestamp: new Date().toISOString()
            };

            // 서버로 메시지 전송
            stompClient.send(`/app/chat.message/${currentChatRoomId}`, {}, JSON.stringify(message));
            $('#messageInput').val(''); // 입력란 초기화
        }
    });
    
	// 채팅방 입장 
	function joinChatRoom(roomId) {
	    if (stompClient && roomId) {
	        if (currentChatRoomId) {
	            // 현재 채팅방에서 퇴장 메시지 전송
	            leaveChatRoom(currentChatRoomId);
	            // 이전 채팅방 구독 해제
	            stompClient.unsubscribe(`sub-${currentChatRoomId}`);
	        }
	
	        currentChatRoomId = roomId;
	        
	        // 선택된 채팅방에서 uniqueKey 가져오기
	        const selectedChatRoom = $(`.chat-user[data-room-id="${roomId}"]`);
	        currentUniqueKey = selectedChatRoom.data('unique-key'); 
	        console.log("Selected uniqueKey:", currentUniqueKey);
	
	        // 새로운 채팅방 구독 설정
	        stompClient.subscribe(`/queue/chat.room.${roomId}`, function (messageOutput) {
	            let message;
	            try {
	                message = JSON.parse(messageOutput.body);
	                console.log("Parsed message:", message);
	            } catch (e) {
	                console.log("Received plain text message:", messageOutput.body);
	                message = { content: messageOutput.body };
	            }
	            showMessage(message); // 실시간으로 도착한 메시지를 화면에 출력
	        }, { id: `sub-${roomId}` });
	
	        // **이전 메시지 로드**
	        loadChatMessages(roomId);
	
	        // 채팅방 멤버 목록 가져오기 및 UI 업데이트
	        loadChatRoomMembers(roomId);
	
	        // 사용자 입장 메시지 전송
	        const enterMessage = {
	            senderId: currentUserId,
	            chatRoomId: roomId,
	            content: currentUserName + '님이 입장하셨습니다.'
	        };
	        stompClient.send(`/app/chat.enter/${roomId}`, {}, JSON.stringify(enterMessage));
	    }
	}

	// 채팅방 멤버 목록을 가져와 UI 업데이트
	function loadChatRoomMembers(roomId) {
	    $.ajax({
	        url: `/api/chat/getRoomMemberNamesByUniqueKey/${currentUniqueKey}`, // 서버에서 멤버 목록을 가져오는 API 경로
	        method: 'GET',
	        success: function(response) {
	            console.log("Members in chat room:", response.members);
	            updateChatPartnerUI(response.members); // 멤버 목록 UI 업데이트
	        },
	        error: function(error) {
	            console.error('Error loading chat room members:', error);
	        }
	    });
	}
    
 	// 채팅방에서 퇴장
    function leaveChatRoom(roomId) {
        if (stompClient && roomId) {
            const exitMessage = {
                senderId: currentUserId,
                chatRoomId: roomId,
                content: currentUserName + '님이 퇴장하셨습니다.'
            };
            stompClient.send(`/app/chat.exit/${roomId}`, {}, JSON.stringify(exitMessage));
        }
    }
 	
 	// 페이지 벗어날 때 WebSocket 연결 끊기 및 퇴장 처리
    function checkAndDisconnectSocket() {
        if (stompClient) {
            // 현재 방에서 퇴장 메시지 전송
            leaveChatRoom(currentChatRoomId);

            // WebSocket 연결 해제
            stompClient.disconnect(function () {
                console.log('Disconnected from WebSocket');
            });
        }
    }
    
 	// 이전 메시지 로드
    function loadChatMessages(roomId) {
        return $.ajax({
            url: `/api/chat/getMessages/${roomId}`, // 서버에서 메시지를 가져오는 API 경로
            method: 'GET',
            dataType: 'json',
            success: function(response) {
                console.log("Previous messages loaded:", response);
                $('#messageArea').empty(); // 기존 메시지 삭제
                response.forEach(function(message) {
                    showMessage(message); // 각 메시지를 화면에 표시
                });
                scrollToBottom(); // 메시지 로드 후 스크롤을 최하단으로 이동
            },
            error: function(error) {
                console.error('Error loading messages:', error);
            }
        });
    }
    
 	// 대화상대 초대 
	$('.inviteButton').click(function () {
	    // 현재 선택된 채팅방의 uniqueKey 가져오기
	    if (!currentUniqueKey) {
	        console.error("UniqueKey is undefined.");
	        return;
	    }
	
	    console.log("Selected uniqueKey:", currentUniqueKey);
	
	    // uniqueKey를 쉼표로 구분하여 배열로 변환
	    const uniqueKeyMembers = currentUniqueKey.split(','); // 쉼표로 구분된 멤버 ID 배열
	    
	    console.log("uniqueKeyMembers", uniqueKeyMembers);
	
	    // 전체 멤버 목록 가져오기
	    const allMembers = $('#recipientSelect option').map(function () {
	        return {
	            memberId: $(this).val(),
	            memberName: $(this).text()
	        };
	    }).get();
	    
	    console.log("allMembers", allMembers);
	
	    // 현재 채팅방에 포함되지 않은 멤버들만 필터링
	    const availableMembers = allMembers.filter(member => !uniqueKeyMembers.includes(member.memberId));
	
	    console.log("availableMembers", availableMembers);
	    
	    // 멤버 목록을 모달에 표시
	    const membersCheckboxes = $('#inviteMembersCheckboxes');
	    membersCheckboxes.empty(); // 기존 목록 초기화
	    availableMembers.forEach(member => {
	        membersCheckboxes.append(`
	            <div class="form-check">
	                <input class="form-check-input invite-member-checkbox" type="checkbox" value="${member.memberId}" id="member-${member.memberId}">
	                <label class="form-check-label" for="member-${member.memberId}">
	                    ${member.memberName}
	                </label>
	            </div>
	        `);
	    });
	
	    // 모달 표시
	    $('#inviteModal').modal('show'); // Bootstrap 모달 표시
	});
    
 	// 선택한 멤버들을 그룹에 초대
    $('#confirmInviteButton').click(function () {
        // 체크박스에서 선택된 멤버들을 가져옵니다
        const selectedMembers = $('.invite-member-checkbox:checked').map(function () {
            return $(this).val();
        }).get(); // 체크된 체크박스의 값을 배열로 반환

        if (selectedMembers.length > 0 && currentUserId) {
            axios.post('/api/chat/createGroup', {
                roomId: currentChatRoomId,
                memberIds: selectedMembers,
                currentUserId: currentUserId  // currentUserId를 요청에 포함
            })
            .then(response => {
                const newRoomId = response.data.roomId;
                if (newRoomId) {
                    // 새로운 방으로 이동
                    joinChatRoom(newRoomId);
                    // 채팅방 목록 새로고침
                    loadInitialData();  // loadChatRooms 대신 사용

                    // 새로운 채팅방 정보를 다른 유저들에게 실시간으로 알림
                    stompClient.send(`/app/chat.newRoom`, {}, JSON.stringify({ roomId: newRoomId }));
                } else {
                    console.error('Failed to create a new group chat.');
                }
            })
            .catch(error => {
                console.error('Error creating group chat:', error);
            })
            .finally(() => {
                $('#inviteModal').modal('hide');  // 모달 숨기기
            });
        } else {
            alert('멤버를 선택하세요.');
        }
    });


    // 모달에서 선택 취소
    $('#cancelInviteButton').click(function () {
        $('#inviteModal').modal('hide');
    });
    
 	// 채팅룸 나가기 
    $('.leaveButton').click(function () {
        const roomId = currentChatRoomId;  // 현재 채팅방 ID 가져오기
        const userId = currentUserId;  // 현재 사용자 ID 가져오기

        if (roomId && userId) {
            axios.post('/api/chat/leaveRoom', 
            `roomId=${encodeURIComponent(roomId)}&userId=${encodeURIComponent(userId)}`, 
            {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
            .then(response => {
                console.log(response.data);  // 성공적인 응답 출력

                // UI에서 해당 채팅방을 리스트에서 제거
                $(`.chat-user[data-room-id="${roomId}"]`).remove(); // 선택자 수정
                
                // chatPartnerImages에서 나간 사용자의 이미지 제거
                $(`.chat-partner-img[data-user-id="${userId}"]`).remove(); // 선택자 수정
                
                // 현재 채팅방 ID 초기화
                currentChatRoomId = null;
                
                // 채팅방이 선택되지 않은 상태를 표시 (d-none 삭제)
                $('.chat-not-selected').removeClass('d-none');
            })
            .catch(error => {
                console.error('Error leaving the room:', error);
            });
        } else {
            console.error('Invalid room ID or user ID.');
        }
    });
    
    $(".search-chat").on("keyup", function () {
	  var value = $(this).val().toLowerCase();
	  $(".chat-users li").filter(function () {
	    $(this).toggle($(this).text().toLowerCase().indexOf(value) > -1);
	  });
	});


 	// 페이지 벗어날 때 퇴장 처리 및 WebSocket 연결 종료
    window.addEventListener('beforeunload', checkAndDisconnectSocket);
 
    // 초기 데이터 및 STOMP 연결 설정
    loadInitialData();
    connect();
});
