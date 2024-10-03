$(document).ready(function () {
	// ===== 전역 변수 선언 =====
    let stompClient = null;
    let currentUserId = $('.userId').text().trim();
    let currentUserName = $('.userName').text().trim();
    let currentUserEmail = null;
    let currentUserImage = null;
    let currentUniqueKey = null;
    let currentChatRoomId = null;

    // ========== 공통 함수 ==========
    /**
     * Ajax를 통해 데이터를 가져오는 함수
     * @param {string} url - 요청할 URL
     * @param {function} onSuccess - 성공 시 호출할 콜백 함수
     */
    function fetchData(url, onSuccess) {
        $.ajax({
            url: url,
            method: 'GET',
            success: onSuccess,
            error: function (error) {
                console.error(`Error fetching data from ${url}:`, error);
            }
        });
    }

<<<<<<< HEAD
    function handleUserStatus(message) {
        if (!message.content) return;
        if (message.content.includes("접속하셨습니다")) {
            updateBadgeStatus(message.senderId, "online");
        } else if (message.content.includes("로그아웃 하셨습니다")) {
            updateBadgeStatus(message.senderId, "offline");
        }
    }

    // chatPartner 채팅창 뱃지 업데이트 
    function updateBadgeStatus(userId, status) {
        const badgeClass = status === 'online' ? 'bg-success' : 'bg-light';
        const userBadge = $(`.chat-user[data-user-id="${userId}"] .badge`);
        if (userBadge.length) {
            userBadge.attr('class', `position-absolute bottom-0 end-0 p-1 badge rounded-pill ${badgeClass}`);
        } else {
            console.error(`Could not find user with ID ${userId} to update status.`);
        }
    }

    // chatList 뱃지
    function createMemberImageHTMLWithoutBadge(memberName, memberId) {
        const imageUrl = `/member/showImageAtMain/${memberId}`;  // 동적으로 이미지 URL 생성
        return `
=======
	// chatPartner 채팅창 뱃지 업데이트 
    function updateBadgeStatus(userId, status) {
	    const badgeClass = status === 'online' ? 'bg-success' : 'bg-light';
	    const userBadge = $(`.chat-user[data-user-id="${userId}"] .badge`);
	    if (userBadge.length) {
	        userBadge.attr('class', `position-absolute bottom-0 end-0 p-1 badge rounded-pill ${badgeClass}`);
	    } else {
	        console.error(`Could not find user with ID ${userId} to update status.`);
	    }
	}
	
	// chatList 
	function createMemberImageHTMLWithoutBadge(memberName, memberId) {
		const imageUrl = `/member/showImageAtMain/${memberId}`;  // 동적으로 이미지 URL 생성
	    return `
>>>>>>> 4f25cbb253b20a874f3d1f475b291f34d462dae4
	        <span class="position-relative chat-user" data-user-id="${memberId}">
	        	<img src="${imageUrl}" alt="${memberName}" width="48" height="48" class="rounded-circle ms-1" />
	        </span>
	    `;
    }

	// 채팅방리스트의 채팅방 항목을 생성하는 함수 
    function createChatRoomHTML(room, memberNames, imagesHtml) {
        return `
            <li>
                <a href="javascript:void(0)" class="px-4 py-3 bg-hover-light-black d-flex align-items-start justify-content-between chat-user bg-light-subtle"
                   data-room-id="${room.id}" data-unique-key="${room.uniqueKey}">
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
    }
<<<<<<< HEAD

    // chatPartnerImages html 
=======
	
	// 채팅방 멤버 이미지를 생성하는 함수 (채팅방 상단용)
>>>>>>> 4f25cbb253b20a874f3d1f475b291f34d462dae4
    function createMemberImageHTML(memberName, memberId, status) {
        const imageUrl = `/member/showImageAtMain/${memberId}`;
        const badgeClass = status === 'online' ? 'bg-success' : 'bg-light';
        return `
            <span class="position-relative chat-user" data-user-id="${memberId}">
                <img src="${imageUrl}" alt="${memberName}" alt="${memberName}" width="48" height="48" class="rounded-circle ms-1" />
                <span class="position-absolute bottom-0 end-0 p-1 badge rounded-pill ${badgeClass}">
                    <span class="visually-hidden">New alerts</span>
                </span>
            </span>
        `;
    }

    // ========== 초기 데이터 로드 및 처리 ==========
    /**
	 * 초대 데이터를 로드하는 함수 
	 */
    function loadInitialData() {
        fetchData('/api/chat/chatData', handleInitialData);
    }

	/**
     * 초기 데이터를 처리하는 함수
     * @param {object} response - 서버로부터 받은 데이터
     */
    function handleInitialData(response) {
        updateRecipientSelect(response.members);
        updateUserInfo(response.currentUser);
        updateChatRooms(response.chatRooms);
    }

	// 사용자 선택 셀렉트를 업데이트하는 함수 ( 새 채팅 시작용)
    function updateRecipientSelect(members) {
	    const recipientSelects = $('.recipientSelect');
	    recipientSelects.each(function() {
	        const recipientSelect = $(this);
	        const currentSelection = recipientSelect.val();  // 현재 선택된 값 저장
	        recipientSelect.empty();
	        
	        // 기본 옵션 추가
	        recipientSelect.append('<option value="" disabled selected>대화 상대를 선택하세요</option>');
	        
	        members.forEach(function (member) {
	            if (member.memberId !== currentUserId) {
	                recipientSelect.append(`<option value="${member.memberId}">${member.memberName}</option>`);
	            }
	        });
	        
	        // 이전에 선택한 값이 있으면 다시 선택 설정
	        if (currentSelection && recipientSelect.find(`option[value="${currentSelection}"]`).length > 0) {
	            recipientSelect.val(currentSelection);
	        } else {
	            recipientSelect.val('');  // 기본 옵션 선택
	        }
	    });
	}
	
	// 현재 사용자 정보 업데이트하는 함수 
    function updateUserInfo(currentUser) {
        if (currentUser && currentUser.memberId) {
            currentUserName = currentUser.memberName;
            currentUserId = currentUser.memberId.trim();
            currentUserEmail = currentUser.memberEmail;
            currentUserMemberGroup = currentUser.memberGroup;
            currentUserImage = `/member/showImageAtMain/${currentUserId}`;

            $('.currentUserName').text(currentUserName);
            $('.currentUserId').text(currentUserId);
            $('.currentUserMemberGroup').text(currentUserMemberGroup);
            $('.currentUserEmail').text(currentUserEmail);
            $('.userProfileImage').attr('src', currentUserImage);
        } else {
            console.error("Invalid user data.");
        }
    }
	
	
	// 채팅방 리스트 업데이트하는 함수 
    function updateChatRooms(chatRooms) {
        $('.chatList').empty();
        chatRooms.forEach(function (room) {
            renderChatRoom(room);
        });
    }

	// 개별 채팅방을 렌더링하는 함수 
    function renderChatRoom(room) {
<<<<<<< HEAD
        let imagesHtml = '';
        let memberNames = '';
        currentUniqueKey = room.uniqueKey;

        if (currentUniqueKey) {
            const members = currentUniqueKey.split(',');
            members.forEach(function (memberName, index) {
                // ChatList에서는 배지를 적용하지 않음
                imagesHtml += createMemberImageHTMLWithoutBadge(memberName, room.members[index]?.memberId);
                memberNames += memberName + (index < members.length - 1 ? ', ' : '');
            });
        } else {
            console.error('currentUniqueKey is undefined!');
        }

        const roomListHtml = createChatRoomHTML(room, memberNames, imagesHtml);
        $('.chatList').append(roomListHtml);
        registerChatRoomClickEvent();
    }



=======
	    let imagesHtml = '';
	    let memberNames = '';
	    console.log("Room Unique Key:", room.uniqueKey);
	
	    if (room.members && room.members.length > 0) {
	        room.members.forEach(function (member, index) {
	            // ChatList에서는 배지를 적용하지 않음
	            imagesHtml += createMemberImageHTMLWithoutBadge(member.name, member.memberId);
	            memberNames += member.name + (index < room.members.length - 1 ? ', ' : '');
	        });
	    } else {
	        console.error('room.members is undefined or empty!');
	    }
	
	    const roomListHtml = createChatRoomHTML(room, memberNames, imagesHtml);
	    $('.chatList').append(roomListHtml);
	    registerChatRoomClickEvent();
	}
	
	
	// ===== 채팅방 입장 및 퇴장 관리 =====
    /**
     * 채팅방에 입장하는 함수
     * @param {number} roomId - 채팅방 ID
     * @param {string} uniqueKey - 채팅방 고유 키
     */
>>>>>>> 4f25cbb253b20a874f3d1f475b291f34d462dae4
    function joinChatRoom(roomId, uniqueKey) {
        if (stompClient && roomId) {
            if (currentChatRoomId) {
                leaveChatRoom(currentChatRoomId);
            }
            currentChatRoomId = roomId;
            currentUniqueKey = uniqueKey;
            $('.chat-not-selected').addClass('d-none');
            stompClient.subscribe(`/queue/chat.room.${roomId}`, function (messageOutput) {
                const message = JSON.parse(messageOutput.body);

                console.log('Received message:', message);  // 메시지 확인용 로그

                // 본인이 보낸 메시지인 경우, 처리하지 않음
                if (message.senderId === currentUserId) {
                    return;  // 본인의 상태 업데이트는 무시
                }

                if (message.content && message.senderName) {
                    if (message.content.includes("접속하셨습니다")) {
                        console.log(`User ${message.senderId} joined`);
                        updateBadgeStatus(message.senderId, "online");  // 상태 업데이트
                    } else if (message.content.includes("로그아웃 하셨습니다")) {
                        console.log(`User ${message.senderId} logged out`);
                        updateBadgeStatus(message.senderId, "offline");  // 상태 업데이트
                    }
                    showMessage(message);  // 메시지 표시
                } else {
                    console.error("Invalid message received:", message);
                }
            }, { id: `sub-${roomId}` });
            updateStatus("online", roomId);
            loadChatMessages(roomId);
            loadChatRoomMembers(roomId, uniqueKey);
            sendEnterMessage(roomId);
        }
    }
<<<<<<< HEAD

    function sendEnterMessage(roomId) {
        if (stompClient && roomId) {
            const enterMessage = {
                senderId: currentUserId,
                chatRoomId: roomId,
                content: `${currentUserName}님이 입장하셨습니다.`,
                timestamp: new Date().toISOString()
            };
            stompClient.send(`/app/chat.enter/${roomId}`, {}, JSON.stringify(enterMessage));
        }
    }

=======
    
    /**
	 * 채팅방 입장 시 메시지 전송 함수 
	 */
    function sendEnterMessage(roomId) {
	    if (stompClient && roomId) {
	        const enterMessage = {
	            senderId: currentUserId,
	            chatRoomId: roomId,
	            content: `${currentUserName}님이 입장하셨습니다.`,
	            timestamp: new Date().toISOString()
	        };
	        stompClient.send(`/app/chat.enter/${roomId}`, {}, JSON.stringify(enterMessage));
	    }
	}
	
	/**
     * 채팅방에서 나가는 함수
     * @param {number} roomId - 채팅방 ID
     */
>>>>>>> 4f25cbb253b20a874f3d1f475b291f34d462dae4
    function leaveChatRoom(roomId) {
        if (stompClient && roomId) {
            sendExitMessage(roomId);
            updateStatus("offline", roomId);
            stompClient.unsubscribe(`sub-${roomId}`);
        }
    }
<<<<<<< HEAD

    function sendExitMessage(roomId) {
        if (stompClient && roomId) {
            const exitMessage = {
                senderId: currentUserId,
                chatRoomId: roomId,
                content: `${currentUserName}님이 퇴장하셨습니다.`,
                timestamp: new Date().toISOString()
            };
            stompClient.send(`/app/chat.exit/${roomId}`, {}, JSON.stringify(exitMessage));
        }
    }
=======
    
    /**
	 * 채팅방 퇴장 시 메시지 전송 함수 
	 */
    function sendExitMessage(roomId) {
	    if (stompClient && roomId) {
	        const exitMessage = {
	            senderId: currentUserId,
	            chatRoomId: roomId,
	            content: `${currentUserName}님이 퇴장하셨습니다.`,
	            timestamp: new Date().toISOString()
	        };
	        stompClient.send(`/app/chat.exit/${roomId}`, {}, JSON.stringify(exitMessage));
	    }
	}
>>>>>>> 4f25cbb253b20a874f3d1f475b291f34d462dae4


	// ===== 메시지 로드 및 표시 =====
    /**
     * 채팅 메시지를 로드하는 함수
     * @param {number} roomId - 채팅방 ID
     */
    function loadChatMessages(roomId) {
        fetchData(`/api/chat/getMessages/${roomId}`, function (response) {
            $('#messageArea').empty();
            response.forEach(function (message) {
                showMessage(message);
            });
            scrollToBottom();
        });
    }
	
	// 채팅방 멤버를 로드하는 함수 
    function loadChatRoomMembers(roomId, uniqueKey) {
	    const encodedUniqueKey = encodeURIComponent(uniqueKey); // uniqueKey 인코딩
	    fetchData(`/api/chat/getRoomMemberNamesByUniqueKey/${encodedUniqueKey}`, function (response) {
	        updateChatPartnerUI(response.members);
	    });
	}
	
	// 채팅방 상단의 멤버 정보를 업데이트하는 함수 
    function updateChatPartnerUI(members) {
<<<<<<< HEAD
        let imagesHtml = '';
        let namesHtml = '';
        const activeMembers = members.filter(member => member.deleted === 0);
        activeMembers.forEach(function (member) {
            imagesHtml += createMemberImageHTML(member.name, member.memberId, member.status);
            namesHtml += `${member.name} (${member.memberGroup}), `;
        });
        $('.chatPartnerImages').html(imagesHtml);
        $('.chatPartnerName').text(namesHtml.slice(0, -2));
        activeMembers.forEach(function (member) {
            updateBadgeStatus(member.memberId, member.status);
        });
    }

=======
		console.log('members:', members);
	    let imagesHtml = '';
	    let namesHtml = '';
	    const activeMembers = members.filter(member => member.deleted === 0);
	    activeMembers.forEach(function (member) {
	        imagesHtml += createMemberImageHTML(member.name, member.memberId, member.status);
	        namesHtml += `${member.name} (${member.memberGroup}), `;
	    });
	    $('.chatPartnerImages').html(imagesHtml);
	    $('.chatPartnerName').text(namesHtml.slice(0, -2));
	    activeMembers.forEach(function(member) {
	        updateBadgeStatus(member.memberId, member.status);
	    });
	}
	
	// 사용자 상태를 서버에 업데이트하는 함수 
>>>>>>> 4f25cbb253b20a874f3d1f475b291f34d462dae4
    function updateStatus(status, roomId) {
        $.ajax({
            url: '/api/chat/updateStatus',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                roomId: roomId,
                userId: currentUserId,
                status: status
            }),
            success: function (response) {
                console.log(`Status updated to: ${status}`);
            },
            error: function (error) {
                console.error('Error updating status:', error);
            }
        });
    }
    
    // ===== 메시지 처리 =====
    /**
     * 메시지를 화면에 표시하는 함수
     * @param {object} message - 메시지 객체
     */
    function showMessage(message) {
        const messageHtml = message.senderId === currentUserId
            ? generateSentMessageHtml(message)
            : generateReceivedMessageHtml(message);
        $('#messageArea').append(messageHtml);
        scrollToBottom();
    }
	
	// 회원이 보낸 메시지 html 
    function generateSentMessageHtml(message) {
        //const timeString = new Date(message.timestamp).toLocaleTimeString();
        const messageDate = new Date(message.timestamp);
        const hours = messageDate.getHours().toString().padStart(2, '0');
        const minutes = messageDate.getMinutes().toString().padStart(2, '0');
        const timeString = `${hours}:${minutes}`;
        return `
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

	// 회원이 받은 메시지 html
    function generateReceivedMessageHtml(message) {
<<<<<<< HEAD
        const senderId = message.senderId;
        const imageUrl = `/member/showImageAtMain/${senderId}`;
        const messageDate = new Date(message.timestamp);
        const hours = messageDate.getHours().toString().padStart(2, '0');
        const minutes = messageDate.getMinutes().toString().padStart(2, '0');
        const timeString = `${hours}:${minutes}`;
=======
		const imageUrl = `/member/showImageAtMain/${message.senderId}`;
        const timeString = new Date(message.timestamp).toLocaleTimeString();
>>>>>>> 4f25cbb253b20a874f3d1f475b291f34d462dae4
        return `
            <div class="hstack gap-3 align-items-start mb-7 justify-content-start">
                 <img src="${imageUrl}" alt="${message.senderName}" width="40" height="40" class="rounded-circle userProfileImage" />
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
	
	// 채팅영역 스크롤해서 최신 메시지로 이동하는 함수 
    function scrollToBottom() {
        const chatBox = document.querySelector('.chat-box-inner');
        chatBox.scrollTop = chatBox.scrollHeight;
    }

	// ===== 이벤트 핸들러 등록 =====
    /**
     * 채팅방 클릭 이벤트 등록
     */
    function registerChatRoomClickEvent() {
        $('.chat-user').off('click').on('click', function () {
            const selectedRoomId = $(this).data('room-id');
            const selectedUniqueKey = $(this).data('unique-key');
            console.log('Clicked chat room:', selectedRoomId, selectedUniqueKey); // 추가
            if (selectedRoomId) {
                $('.chat-not-selected').addClass('d-none');
                joinChatRoom(selectedRoomId, selectedUniqueKey);
            }
        });
    }

    // 채팅방을 시작하는 기능
<<<<<<< HEAD
    $('.startChat').click(function () {
        const recipientId = $('.recipientSelect').val();  // 선택한 상대방의 ID
        if (recipientId) {
            axios.post('/api/chat/start', `recipientId=${encodeURIComponent(recipientId)}`, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                }
            })
                .then(response => {
                    const chatRoomId = response.data.id; // 서버에서 반환된 채팅방 ID
                    if (chatRoomId) {
                        // 새 채팅방으로 입장
                        joinChatRoom(chatRoomId);

                        // 초기 데이터를 로드하여 UI 갱신
                        loadInitialData();

                        // 상대방에게 새로운 채팅방이 생겼음을 알림
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

            showMessage(message);
        }
    });

    // 초대하기 버튼 클릭 이벤트
    $('.inviteButton').click(function () {
        if (!currentUniqueKey) {
            console.error("UniqueKey is undefined.");
            return;
        }

        console.log("Selected uniqueKey:", currentUniqueKey);

        const uniqueKeyMembers = currentUniqueKey.split(','); // 쉼표로 구분된 멤버 ID 배열

        // 서버로부터 방의 전체 멤버 목록을 가져옴 (deleted 상태 포함)
        $.ajax({
            url: `/api/chat/getRoomMemberNamesByUniqueKey/${currentUniqueKey}`,  // 서버에서 멤버 목록을 가져오는 API 경로
            method: 'GET',
            success: function (response) {
                const allMembers = response.members;
                const availableMembers = allMembers.filter(member => member.deleted === 1 && !uniqueKeyMembers.includes(member.memberId));

                // 멤버 목록을 모달에 표시
                const membersCheckboxes = $('#inviteMembersCheckboxes');
                membersCheckboxes.empty(); // 기존 목록 초기화
                availableMembers.forEach(member => {
                    membersCheckboxes.append(`
=======
	$('.startChat').click(function (event) {
		event.preventDefault();
	    const recipientSelect = $(this).siblings('.recipientSelect');
	    console.log("RecipientSelect options at click time:");
	    recipientSelect.find('option').each(function() {
	        console.log($(this).val(), $(this).text());
	    });
	    const recipientId = recipientSelect.val();  // 선택한 상대방의 ID
	    console.log("Selected recipientId:", recipientId);
	    if (recipientId) {
	        axios.post('/api/chat/start', `recipientId=${encodeURIComponent(recipientId)}`, {
	            headers: {
	                'Content-Type': 'application/x-www-form-urlencoded'
	            }
	        })
	        .then(response => {
	            const chatRoomId = response.data.id; // 서버에서 반환된 채팅방 ID
	            const uniqueKey = response.data.uniqueKey; // 서버에서 반환된 uniqueKey
	            if (chatRoomId && uniqueKey) {
	                // 새 채팅방으로 입장
	                joinChatRoom(chatRoomId, uniqueKey);  
	
	                // 초기 데이터를 로드하여 UI 갱신
	                loadInitialData(); 
	
	                // 상대방에게 새로운 채팅방이 생겼음을 알림
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
    
    // 메시지 전송 버튼 클릭 이벤트 
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
	
	        // 메시지를 화면에 바로 추가
	        showMessage(message);
	
	        // 입력란 초기화
	        $('#messageInput').val('');
	    }
	});
    
    // 초대하기 버튼 클릭 이벤트
	$('.inviteButton').click(function () {
	    if (!currentUniqueKey) {
	        console.error("UniqueKey is undefined.");
	        return;
	    }
	
	    console.log("Selected uniqueKey:", currentUniqueKey);
	
	    // 서버로부터 초대 가능한 멤버 목록을 가져옴
	    $.ajax({
	        url: `/api/chat/getAvailableMembersForInvitation/${currentUniqueKey}`,
	        method: 'GET',
	        success: function (response) {
	            const availableMembers = response.availableMembers;
	            console.log("availableMembers", availableMembers);
	
	            // 멤버 목록을 모달에 표시
	            const membersCheckboxes = $('#inviteMembersCheckboxes');
	            membersCheckboxes.empty(); // 기존 목록 초기화
	            availableMembers.forEach(member => {
	                membersCheckboxes.append(`
>>>>>>> 4f25cbb253b20a874f3d1f475b291f34d462dae4
	                    <div class="form-check">
	                        <input class="form-check-input invite-member-checkbox" type="checkbox" value="${member.memberId}" id="member-${member.memberId}">
	                        <label class="form-check-label" for="member-${member.memberId}">
	                            ${member.name}
	                        </label>
	                    </div>
	                `);
<<<<<<< HEAD
                });

                // 모달 표시
                $('#inviteModal').modal('show');
            },
            error: function (error) {
                console.error('Error fetching member list:', error);
            }
        });
    });

    // 초대 멤버 확정 버튼
    $('#confirmInviteButton').click(function () {
        const selectedMembers = $('.invite-member-checkbox:checked').map(function () {
            return $(this).val();
        }).get();

        if (selectedMembers.length > 0 && currentUserId) {
            axios.post('/api/chat/createGroup', {
                roomId: currentChatRoomId,
                memberIds: selectedMembers,
                currentUserId: currentUserId
            })
                .then(response => {
                    const newRoomId = response.data.roomId;
                    if (newRoomId) {
                        joinChatRoom(newRoomId);
                        loadInitialData();
                        stompClient.send(`/app/chat.newRoom`, {}, JSON.stringify({ roomId: newRoomId }));
                    }
                })
                .catch(error => {
                    console.error('Error creating group chat:', error);
                })
                .finally(() => {
                    $('#inviteModal').modal('hide');
                });
        } else {
            alert('멤버를 선택하세요.');
        }
    });

    // 나가기 버튼 클릭 이벤트
    $('.leaveButton').click(function () {
        const roomId = currentChatRoomId;
        const userId = currentUserId;

        if (roomId && userId) {
            axios.post('/api/chat/leaveRoom',
                `roomId=${encodeURIComponent(roomId)}&userId=${encodeURIComponent(userId)}`,
                {
                    headers: {
                        'Content-Type': 'application/x-www-form-urlencoded'
                    }
                })
                .then(response => {
                    console.log(response.data);

                    // UI에서 해당 채팅방을 리스트에서 제거
                    $(`.chat-user[data-room-id="${roomId}"]`).remove();

                    // chatPartnerImages에서 나간 사용자의 이미지 제거
                    $(`.chat-partner-img[data-user-id="${userId}"]`).remove();

                    // 현재 채팅방 ID 초기화
                    currentChatRoomId = null;

                    // 채팅방이 선택되지 않은 상태를 표시
                    $('.chat-not-selected').removeClass('d-none');

                    leaveChatRoom(roomId);
                })
                .catch(error => {
                    console.error('Error leaving the room:', error);
                });
        } else {
            console.error('Invalid room ID or user ID.');
        }
    });
=======
	            });
	
	            // 모달 표시
	            $('#inviteModal').modal('show');
	        },
	        error: function (error) {
	            console.error('Error fetching member list:', error);
	        }
	    });
	});
	
	// 초대 멤버 확정 버튼
	$('#confirmInviteButton').click(function () {
	    const selectedMembers = $('.invite-member-checkbox:checked').map(function () {
	        return $(this).val();
	    }).get();
	
	    if (selectedMembers.length > 0 && currentUserId) {
	        axios.post('/api/chat/createGroup', {
	            roomId: currentChatRoomId,
	            memberIds: selectedMembers,
	            currentUserId: currentUserId
	        })
	        .then(response => {
	            const newRoomId = response.data.roomId;
	            const newUniqueKey = response.data.uniqueKey;
	            const newMembers = response.data.members;
	            if (newRoomId && newUniqueKey && newMembers) {
	                // 새로운 채팅방을 채팅방 목록에 추가
	                renderChatRoom({
	                    id: newRoomId,
	                    uniqueKey: newUniqueKey,
	                    members: newMembers
	                });
	
	                // 새로운 채팅방으로 입장
	                joinChatRoom(newRoomId, newUniqueKey);
	
	                // 새로운 채팅방이 생겼음을 상대방들에게 알림
	                selectedMembers.forEach(memberId => {
	                    stompClient.send(`/user/${memberId}/queue/newChatRoom`, {}, JSON.stringify({
	                        roomId: newRoomId,
	                        uniqueKey: newUniqueKey,
	                        members: newMembers
	                    }));
	                });
	            }
	        })
	        .catch(error => {
	            console.error('Error creating group chat:', error);
	        })
	        .finally(() => {
	            $('#inviteModal').modal('hide');
	        });
	    } else {
	        alert('멤버를 선택하세요.');
	    }
	});
	
	// 나가기 버튼 클릭 이벤트
	$('.leaveButton').click(function () {
	    const roomId = currentChatRoomId;
	    const userId = currentUserId;
	
	    if (roomId && userId) {
	        axios.post('/api/chat/leaveRoom', 
	        `roomId=${encodeURIComponent(roomId)}&userId=${encodeURIComponent(userId)}`, 
	        {
	            headers: {
	                'Content-Type': 'application/x-www-form-urlencoded'
	            }
	        })
	        .then(response => {
	            console.log(response.data); 
	
	            // UI에서 해당 채팅방을 리스트에서 제거
	            $(`.chat-user[data-room-id="${roomId}"]`).remove();
	
	            // chatPartnerImages에서 나간 사용자의 이미지 제거
	            $(`.chat-partner-img[data-user-id="${userId}"]`).remove();
	
	            // 현재 채팅방 ID 초기화
	            currentChatRoomId = null;
	
	            // 채팅방이 선택되지 않은 상태를 표시
	            $('.chat-not-selected').removeClass('d-none');
	
	            leaveChatRoom(roomId);
	        })
	        .catch(error => {
	            console.error('Error leaving the room:', error);
	        });
	    } else {
	        console.error('Invalid room ID or user ID.');
	    }
	});
>>>>>>> 4f25cbb253b20a874f3d1f475b291f34d462dae4

    // ========== WebSocket 연결 및 초기 데이터 로드 ==========
    function connect() {
	    const socket = new SockJS('/stomp/chat');
	    stompClient = Stomp.over(socket);
	    stompClient.connect({}, function (frame) {
	        console.log('Connected: ' + frame);
	
	        // 새로운 채팅방 알림 구독
	        stompClient.subscribe(`/user/${currentUserId}/queue/newChatRoom`, function (message) {
	            const newRoomData = JSON.parse(message.body);
	            renderChatRoom(newRoomData);
	        });
	
	        if (currentChatRoomId && currentUniqueKey) {
	            joinChatRoom(currentChatRoomId, currentUniqueKey);
	        }
	    }, function (error) {
	        console.log("WebSocket connection closed:", error);
	        updateStatus("offline");
	    });
	}

    // 페이지를 벗어날 때 WebSocket 연결 종료 및 퇴장 처리
    window.addEventListener('beforeunload', function () {
        if (stompClient) {
            leaveChatRoom(currentChatRoomId);
            stompClient.disconnect(function () {
                console.log('Disconnected from WebSocket');
            });
        }
    });

    // 초기 데이터 및 STOMP 연결 설정
    loadInitialData();
    connect();
});