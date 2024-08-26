$(document).ready(function () {
    let stompClient = null;
    let currentChatRoomId = null;
    let currentDate = null;

    function loadInitialData() {
        $.ajax({
            url: '/api/chat/chatData',
            method: 'GET',
            success: function(response) {
                $('#userName').text(response.currentUserName);
                $('#userId').text(response.currentUserId);

                // 채팅방 목록 로드
                updateChatRooms(response.chatRooms);

                // 대화 상대 목록 로드
                updateRecipientSelect(response.members);
            },
            error: function(error) {
                console.error('Error loading initial data:', error);
            }
        });
    }

    function loadChatRooms() {
        axios.get('/api/chat/getRooms')
            .then(response => {
                const rooms = response.data;
                const chatRoomList = document.getElementById('chatList'); // 채팅방 리스트를 표시할 요소
                chatRoomList.innerHTML = ''; // 기존 목록을 초기화합니다.

                rooms.forEach(room => {
                    const roomElement = document.createElement('div');
                    roomElement.classList.add('chatRoom');
                    roomElement.innerHTML = room.name;
                    roomElement.setAttribute('data-room-id', room.id);
                    roomElement.addEventListener('click', function () {
                        joinChatRoom(room.id);
                    });
                    chatRoomList.appendChild(roomElement);
                });
            })
            .catch(error => {
                console.error('Error loading chat rooms:', error);
            });
    }

    function updateChatRooms(chatRooms) {
        chatRooms.forEach(function(room) {
            const existingRoomElement = $(`#chatList .chatRoom[data-room-id="${room.id}"]`);
            if (existingRoomElement.length === 0) { // 방이 목록에 없다면 추가
                $('#chatList').append(`<div class="chatRoom" data-room-id="${room.id}">${room.name}</div>`);
            }
        });
        registerChatRoomClickEvent(); // 새로 추가된 방에 대해 클릭 이벤트 등록
    }

    function updateRecipientSelect(members) {
        $('#recipientSelect').empty();
        members.forEach(function(member) {
            $('#recipientSelect').append(`<option value="${member.memberId}">${member.memberName}</option>`);
        });
    }

    function registerChatRoomClickEvent() {
        $('.chatRoom').off('click').on('click', function () {
            currentChatRoomId = $(this).data('room-id');
            
            if (currentChatRoomId) {
                joinChatRoom(currentChatRoomId);
            } else {
                console.error('Invalid room ID:', currentChatRoomId);
            }
        });
    }

    function connect() {
        const socket = new SockJS('/stomp/chat');
        stompClient = Stomp.over(socket);
        stompClient.connect({}, function (frame) {
            console.log('Connected: ' + frame);
        
            // 새로운 방이 생성되었을 때 알림을 받는 구독
            const userId = $('#userId').text();
            stompClient.subscribe(`/user/${userId}/queue/newChatRoom`, function (messageOutput) {
                console.log("Received new chat room:", messageOutput.body); // 로그 추가
                const chatRoom = JSON.parse(messageOutput.body);
                updateChatRooms([chatRoom]);  // 새로운 방을 기존 목록에 추가
            });
        
            if (currentChatRoomId) {
                joinChatRoom(currentChatRoomId);
            }
        });
    }

    function joinChatRoom(roomId) {
        if (stompClient && roomId) {
            // 현재 구독된 채팅방과 다른 방을 구독하려고 할 때만 기존 구독 해지
            if (currentChatRoomId && currentChatRoomId !== roomId) {
                stompClient.unsubscribe(currentChatRoomId);
            }

            currentChatRoomId = roomId;
            currentDate = null;  // 새로운 채팅방에 접속할 때마다 currentDate 초기화

            const queue = `/queue/chat.room.${roomId}`;

            // 채팅방 구독
            stompClient.subscribe(queue, function (messageOutput) {
			    console.log("Received message:", messageOutput.body);
                const message = JSON.parse(messageOutput.body);
                showMessage(message);
            });

            const enterMessage = {
                senderId: $('#userId').text(),
                content: "",
                timestamp: new Date().toISOString()
            };

            // 입장 메시지 전송
            stompClient.send(`/app/chat.enter/${roomId}`, {}, JSON.stringify(enterMessage));

            // 이전 메시지 로드
            axios.get(`/chatRoom/getMessages/${roomId}`)
                .then(response => {
                    $('#messageArea').empty(); // 메시지 영역 초기화
                    response.data.forEach(message => {
                        showMessage(message); // 각 메시지를 표시
                    });
                })
                .catch(error => {
                    console.error('Error loading messages:', error);
                });

            // 멤버 상태 업데이트
            axios.get(`/api/chat/getRoomMembers/${roomId}`)
                .then(memberResponse => {
                    const members = memberResponse.data.members;
                    const statuses = members.map(member => `${member.name}: ${member.status}`).join(', ');
                    $('#chatUserStatus').text(statuses);

                    // 채팅방 이름은 첫 번째 멤버 이름을 기준으로 표시
                    if (members.length > 0) {
                        $('#chatUserName').text(members[0].name);
                    }
                })
                .catch(error => {
                    console.error('Error loading room members:', error);
                });
        } else {
            console.error('Invalid chat room ID:', roomId);
        }
    }

    function showMessage(message) {
        const currentUserId = $('#userId').text();

        if (typeof message === 'string') {
            try {
                // 문자열일 경우 JSON으로 파싱 시도
                message = JSON.parse(message);
            } catch (e) {
				console.error('Message is not a valid JSON:', message);
				return;
			}
		}
		
		// 메시지 객체의 유효성 검사
		if (!message || typeof message.senderId === 'undefined' || 
			typeof message.content === 'undefined' || typeof message.timestamp === 'undefined') {
				
			console.error('Invalid message format:', message);
			return;
	    }
	    
	    const senderName = message.senderName || message.senderId; // senderName을 우선 사용, 없으면 senderId 사용
	
	    // 메시지의 날짜와 시간을 포맷팅
	    const messageDate = new Date(message.timestamp);
	    const hours = messageDate.getHours().toString().padStart(2, '0');
	    const minutes = messageDate.getMinutes().toString().padStart(2, '0');
	    const timeString = `${hours}:${minutes}`;
	    
	    const messageDateString = messageDate.toISOString().split('T')[0].replace(/-/g, '.'); // 날짜를 "YYYY.MM.DD" 형식으로 변환
	
	    // 정각 기준으로 날짜 출력
	    if (currentDate !== messageDateString) {
	        currentDate = messageDateString;
	        $('#messageArea').append(`<div class="date-divider">-------------${currentDate}-------------</div>`);
	    }
	
	    // 나와 상대방의 메시지를 구분하여 다른 클래스를 적용
	    const messageClass = message.senderId === currentUserId ? 'my-message' : 'their-message';
	
	    // 메시지 출력
	    const messageElement = `<div class="${messageClass}"><strong>${senderName}:</strong> ${message.content} <span class="time">${timeString}</span></div>`;
	    $('#messageArea').append(messageElement);
	}
	
	$('#sendButton').click(function () {
	    const content = $('#messageInput').val();
	    if (content && stompClient && currentChatRoomId) {
	        const message = {
	            chatRoomId: currentChatRoomId,
	            senderId: $('#userId').text(),
	            content: content,
	            timestamp: new Date().toISOString()
	        };
	        
	        // 메시지를 전송
	        stompClient.send(`/app/chat.message/${currentChatRoomId}`, {}, JSON.stringify(message));
	        $('#messageInput').val('');
	    }
	});
	
	$('#startChat').click(function () {
	    const recipientId = $('#recipientSelect').val();
	    if (recipientId) {
	        axios.post('/api/chat/start', `recipientId=${encodeURIComponent(recipientId)}`, {
	            headers: {
	                'Content-Type': 'application/x-www-form-urlencoded'
	            }
	        })
	        .then(response => {
	            currentChatRoomId = response.data.id;
	            if (currentChatRoomId) {
	                joinChatRoom(currentChatRoomId);
	                loadChatRooms();
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
	
	$('#inviteButton').click(function () {
	    // 모달을 표시
	    $('#inviteModal').show();
	
	    // 전체 멤버 목록 가져오기
	    const allMembers = $('#recipientSelect option').map(function () {
	        return {
	            memberId: $(this).val(),
	            memberName: $(this).text()
	        };
	    }).get();
	
	    // 현재 채팅방에 속한 멤버 가져오기
	    axios.get(`/chatRoom/getMessages/${currentChatRoomId}`)
	        .then(response => {
	            const existingMemberIds = new Set(response.data.map(message => message.senderId));
	
	            // 현재 채팅방에 속하지 않은 멤버들만 필터링
	            const availableMembers = allMembers.filter(member => !existingMemberIds.has(member.memberId));
	
	            // 멤버 목록을 모달에 표시
	            const membersSelect = $('#inviteMembersSelect');
	            membersSelect.empty(); // 기존 목록 초기화
	            availableMembers.forEach(member => {
	                membersSelect.append(`<option value="${member.memberId}">${member.memberName}</option>`);
	            });
	        })
	        .catch(error => {
	            console.error('Error loading existing members:', error);
	        });
	});
	
	// 모달에서 선택 취소
	$('#cancelInviteButton').click(function () {
	    $('#inviteModal').hide();
	});
	
	// 선택하기 버튼 
	$('#confirmInviteButton').click(function () {
	    const selectedMembers = $('#inviteMembersSelect').val();
	    const currentUserId = $('#userId').text();  // currentUserId를 명확하게 지정
	
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
	                loadChatRooms();  
	
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
	            $('#inviteModal').hide();  // 모달 숨기기
	        });
	    } else {
	        alert('멤버를 선택하세요.');
	    }
	});
	
	loadInitialData();
	connect();
});
	               