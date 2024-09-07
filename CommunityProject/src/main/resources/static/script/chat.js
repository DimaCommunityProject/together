$(document).ready(function () {
	let stompClient = null;
	let currentChatRoomId = null;
	let currentDate = null;

	// 초기 데이터 로드
	function loadInitialData() {
		$.ajax({
			url: '/api/chat/chatData',
			method: 'GET',
			success: function (response) {
				$('#userName').text(response.currentUserName);
				$('#userId').text(response.currentUserId);

				// 채팅방 목록 로드
				updateChatRooms(response.chatRooms);

				// 대화 상대 목록 로드
				updateRecipientSelect(response.members);
			},
			error: function (error) {
				console.error('Error loading initial data:', error);
			}
		});
	}

	// 채팅방 목록 업데이트
	function updateChatRooms(chatRooms) {
		$('#chatList').empty(); // 이전 목록 초기화
		chatRooms.forEach(function (room) {
			const existingRoomElement = $(`#chatList .chatRoom[data-room-id="${room.id}"]`);
			if (existingRoomElement.length === 0) { // 방이 목록에 없다면 추가
				$('#chatList').append(`<div class="chatRoom" data-room-id="${room.id}">${room.name}</div>`);
			}
		});
		registerChatRoomClickEvent(); // 새로 추가된 방에 대해 클릭 이벤트 등록
	}

	// 대화 상대 목록 업데이트
	function updateRecipientSelect(members) {
		$('#recipientSelect').empty();
		members.forEach(function (member) {
			$('#recipientSelect').append(`<option value="${member.memberId}">${member.memberName}</option>`);
		});
	}

	// 채팅방 클릭 이벤트 등록
	function registerChatRoomClickEvent() {
		$('.chatRoom').off('click').on('click', function () {
			const selectedRoomId = $(this).data('room-id');
			if (selectedRoomId) {
				joinChatRoom(selectedRoomId);
			} else {
				console.error('Invalid room ID:', selectedRoomId);
			}
		});
	}

	// STOMP 연결 및 구독 설정
	function connect() {
		const socket = new SockJS('/stomp/chat');
		stompClient = Stomp.over(socket);

		stompClient.connect({}, function (frame) {
			console.log('Connected: ' + frame);
			const userId = $('#userId').text();

			// 현재 사용자의 새로운 채팅방 구독
			stompClient.subscribe(`/user/${userId}/queue/newChatRoom`, function (messageOutput) {
				const chatRoom = JSON.parse(messageOutput.body);
				updateChatRooms([chatRoom]);  // 새로운 방을 기존 목록에 추가
			});

			// 기존 채팅방의 메시지 수신
			if (currentChatRoomId) {
				joinChatRoom(currentChatRoomId);
			}
		});
	}

	// 멤버 상태를 표시하는 함수
	function updateChatUserStatus(members) {
		const statusList = members.map(member =>
			`<div>${member.name} (${member.memberId}): ${member.status}</div>`
		).join('');

		$('#chatUserStatus').html(statusList); // 상태 정보를 HTML로 삽입
	}

	// 채팅방에 참여 및 구독 설정
	function joinChatRoom(roomId) {
		if (stompClient && roomId) {
			if (currentChatRoomId) {
				stompClient.unsubscribe(`sub-${currentChatRoomId}`);
			}

			currentChatRoomId = roomId;
			currentDate = null;

			const queue = `/queue/chat.room.${roomId}`;

			stompClient.subscribe(queue, function (messageOutput) {
				console.log("Received message:", messageOutput.body);
				const message = JSON.parse(messageOutput.body);
				showMessage(message);
			}, { id: `sub-${roomId}` });

			// 이전 메시지 로드
			axios.get(`/chatRoom/getMessages/${roomId}`)
				.then(response => {
					$('#messageArea').empty();
					response.data.forEach(message => {
						showMessage(message);
					});
				})
				.catch(error => {
					console.error('Error loading messages:', error);
				});

			// 멤버 상태 업데이트
			axios.get(`/getRoomMembers/${roomId}`)
				.then(memberResponse => {
					const members = memberResponse.data.members;
					updateChatUserStatus(members);  // 모든 멤버 상태 업데이트
				})
				.catch(error => {
					console.error('Error loading room members:', error);
				});

			// 입장 메시지 전송
			const enterMessage = {
				senderId: $('#userId').text(),
				chatRoomId: roomId,
				content: '' // 입장 메시지 내용은 서버에서 설정
			};
			stompClient.send(`/app/chat.enter/${roomId}`, {}, JSON.stringify(enterMessage));
		} else {
			console.error('Invalid chat room ID:', roomId);
		}
	}

	// 메시지 표시
	function showMessage(message) {
		const currentUserId = $('#userId').text();

		if (typeof message === 'string') {
			try {
				message = JSON.parse(message);
			} catch (e) {
				console.error('Message is not a valid JSON:', message);
				return;
			}
		}

		if (!message || typeof message.senderId === 'undefined' ||
			typeof message.content === 'undefined' || typeof message.timestamp === 'undefined') {
			console.error('Invalid message format:', message);
			return;
		}

		const senderName = message.senderName || message.senderId;

		const messageDate = new Date(message.timestamp);
		const hours = messageDate.getHours().toString().padStart(2, '0');
		const minutes = messageDate.getMinutes().toString().padStart(2, '0');
		const timeString = `${hours}:${minutes}`;

		const messageDateString = messageDate.toISOString().split('T')[0].replace(/-/g, '.');
		if (currentDate !== messageDateString) {
			currentDate = messageDateString;
			$('#messageArea').append(`<div class="date-divider">-------------${currentDate}-------------</div>`);
		}

		// 입장 및 퇴장 메시지 처리
		if (message.content.includes("님이 입장하셨습니다.") || message.content.includes("님이 퇴장하셨습니다.")) {
			const eventClass = 'event-message';
			const eventMessageElement = `<div class="${eventClass}"><em>${message.content}</em> <span class="time">${timeString}</span></div>`;
			$('#messageArea').append(eventMessageElement);

			// 멤버 상태 업데이트
			axios.get(`/getRoomMembers/${message.roomId}`)
				.then(memberResponse => {
					const members = memberResponse.data.members;
					updateChatUserStatus(members);
				})
				.catch(error => {
					console.error('Error loading room members:', error);
				});
		} else {
			const messageClass = message.senderId === currentUserId ? 'my-message' : 'their-message';
			const messageElement = `<div class="${messageClass}"><strong>${senderName}:</strong> ${message.content} <span class="time">${timeString}</span></div>`;
			$('#messageArea').append(messageElement);
		}
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
					currentChatRoomId = response.data.id;
					if (currentChatRoomId) {
						joinChatRoom(currentChatRoomId);
						loadInitialData();  // 전체 초기 데이터 로드, 채팅방 목록 갱신 포함

						// 상대방에게 새로운 채팅방 정보를 알림
						stompClient.send(`/user/${recipientId}/queue/newChatRoom`, {}, JSON.stringify({ roomId: currentChatRoomId }));
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


	// 메시지 전송
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
					$('#inviteModal').hide();  // 모달 숨기기
				});
		} else {
			alert('멤버를 선택하세요.');
		}
	});

	window.onbeforeunload = function () {
		if (stompClient && currentChatRoomId) {
			const exitMessage = {
				senderId: $('#userId').text(),
				chatRoomId: currentChatRoomId,
				content: '' // 퇴장 메시지 내용은 서버에서 설정
			};
			stompClient.send(`/app/chat.exit/${currentChatRoomId}`, {}, JSON.stringify(exitMessage));
		}
	};

	// 초기 데이터 및 STOMP 연결 설정
	loadInitialData();
	connect();
});