<%@page import="com.pyeondeuk.model.MemberDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>편의점 지도</title>
    <link rel="stylesheet" href="resources/css/main3.css" />
	<link rel="stylesheet" href="resources/css/font.css" />
    <script src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=71df7e3003dbbd28ec11f264e023e8c9"></script>
    <style>
        #map { width: 100%; height: 600px; }
       .custom-overlay {
    background-color: rgba(255, 255, 255, 0.7); /* 반투명 배경 */
    backdrop-filter: blur(5px); /* 블러 효과 추가 */
    padding: 10px; /* 패딩 감소 */
    border-radius: 8px; /* 둥근 모서리 */
    text-align: center;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
    line-height: 1.33; /* 줄 간격 축소 */
    font-size: 14px; /* 텍스트 크기 */
    color: #4e555b;
}

.custom-overlay b {
    display: block;
    font-size: 15px; /* 편의점명 크기 */
    margin-bottom: 7px; /* 아래 내용과의 간격 최소화 */
}

.custom-overlay p {
    margin: 2px 0; /* 각 줄 간격 최소화 */
}

.custom-overlay .custom-button {
    margin-top: 5px; 
    background-color: #6c757d; 
    color: #fff;
    border: none;
    padding: 4px 10px; 
    border-radius: 4px;
    font-size: 12px;
    cursor: pointer;
    transition: background-color 0.3s, box-shadow 0.3s;
}

.custom-overlay .custom-button:hover {
    background-color: #5a6268; 
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1); 
}

.custom-overlay .custom-button:active {
    background-color: #4e555b; 
}

#loading {
	position: absolute;
	top: 50px;
	left: 50%;
	transform: translateX(-50%);
	background: rgba(0, 0, 0, 0.7);
	color: white;
	padding: 5px 10px;
	border-radius: 5px;
	display: none;
	z-index: 1000;
}
    </style>
</head>
<body>

<body class="is-preload homepage">
	<div id="page-wrapper">

		<!-- Header -->
		<div id="header-wrapper">
			<header id="header" class="container">

				<!-- Logo -->
				<div id="logo">
					<a href="index.jsp"><img src="resources/images/logo.png" width="200px"></a>
				</div>
				
				<% MemberDTO info =(MemberDTO) session.getAttribute("info"); %>
    

				<!-- Nav -->
				<nav id="nav">
					<ul>
						<li class="current"><a href="index.jsp">메인페이지</a></li>
						<li><a href="#">할인상품</a>
							<ul>
								<li><a href="cu.html"><img src="resources/images/cu.png"  width="50px" ></a></li>
								<li><a href="emart.html"><img src="resources/images/emart.png"  width="70px" ></a></li>
								<li><a href="gs.html"><img src="resources/images/gs.png"  width="60px" ></a></li>
								<li><a href="seven.html"><img src="resources/images/seven.png" width="70px" ></a></li>
							</ul>
						</li>

						<li><a href="#">PB상품</a>
							<ul>
								<li><a href="cu_pb.html"><img src="resources/images/cu.png" width="50px"></a></li>
								<li><a href="emart_pb.html"><img src="resources/images/emart.png" width="70px"></a></li>
								<li><a href="gs_pb.html"><img src="resources/images/gs.png" width="60px"></a></li>
								<li><a href="seven_pb.html"><img src="resources/images/seven.png" width="70px"></a></li>
							</ul>
						</li>

						<li><a href="event.html">이벤트</a></li>
						<li><a href="map.jsp">편의점찾기</a></li>
						<%if(info == null){%>
                  		<li><a href="login.jsp">로그인</a></li>
                  		<%}else{ %>
                     <!-- 로그인 후 Logout.jsp로 이동할 수 있는'로그아웃'링크와 '개인정보수정'링크를 출력하시오. -->
                     <li><a href="LogoutService">로그아웃</a></li>
                     <li><a href="update.jsp">회원정보</a></li>
                  <%} %>
						
					</ul>
				</nav>

			</header>
		</div>
   
    <br>
    <div id="loading">로딩 중...</div>
    <div id="map"></div>

    <script>
    // 편의점 상세 정보 페이지로 이동하는 함수
    function redirectToPage(csName, csNick, csTag, roadAddressName, addressName, rating) {
    console.log('redirectToPage 호출됨:', {
        csName,
        csNick,
        csTag,
        roadAddressName,
        addressName,
        rating
    }); // 디버깅용 로그
    const url = `review.jsp?csName=${encodeURIComponent(csName)}&csNick=${encodeURIComponent(csNick)}&csTag=${encodeURIComponent(csTag)}&roadAddressName=${encodeURIComponent(roadAddressName)}&addressName=${encodeURIComponent(addressName)}&rating=${rating}`;
    console.log('이동할 URL:', url);
     // URL이 제대로 생성되었는지 확인한 후 페이지 이동
        if (url) {
            window.location.href = url;
        } else {
            console.error('URL 생성 실패: 필요한 값이 부족합니다.');
        }
    }

    kakao.maps.load(function () {
        var mapContainer = document.getElementById('map');
        var mapOption = {
            center: new kakao.maps.LatLng(35.1595, 126.8526), // 초기 중심 좌표
            level: 3 // 확대 수준
        };

        var map = new kakao.maps.Map(mapContainer, mapOption);
        var markers = []; // 기존 마커를 관리할 배열

        // 브랜드별 마커 이미지
        var markerImages = {
            1: "./resources/images/emart24_marker.png",  // 이마트24
            2: "./resources/images/cu_marker.png",      // CU
            3: "./resources/images/gs25_marker.png",    // GS25
            4: "./resources/images/seven_marker.png"    // 세븐일레븐
        };

        // 서버에서 데이터를 불러와 마커를 생성하는 함수
        function loadStores(lat, lng) {
            document.getElementById('loading').style.display = 'block';

            fetch('<%=request.getContextPath()%>/getNearbyStores?latitude=' + lat + '&longitude=' + lng + '&radius=1')
                .then(response => response.json())
                .then(data => {
                    document.getElementById('loading').style.display = 'none';

                    // 기존 마커 제거
                    markers.forEach(marker => marker.setMap(null));
                    markers = [];

                    // 새로운 마커 생성
                    data.forEach(store => {
                    	store.csName = store.csName || 'Unknown';
                        store.csNick = store.csNick || 'No Nickname';
                        store.csTag = store.csTag || 'No Tags';
                        store.roadAddressName = store.roadAddressName || 'Unknown Address';
                        store.addressName = store.addressName || 'Unknown Address';
                        store.rating = store.rating || 0;
                        var rating = store.rating; // 기본값 0
                        var starImage;
                        if (rating >= 4.5) starImage = './resources/images/5star.png';
                        else if (rating >= 3.5) starImage = './resources/images/4star.png';
                        else if (rating >= 2.5) starImage = './resources/images/3star.png';
                        else if (rating >= 1.5) starImage = './resources/images/2star.png';
                        else starImage = './resources/images/1star.png';

                        var markerImageUrl = markerImages[store.brandSeq];
                        var markerImage = new kakao.maps.MarkerImage(
                            markerImageUrl,
                            new kakao.maps.Size(38, 50) // 마커 이미지 크기
                        );

                        var marker = new kakao.maps.Marker({
                            map: map,
                            position: new kakao.maps.LatLng(store.latitude, store.longitude),
                            image: markerImage // 마커 이미지 설정
                        });

                        var overlayContent = `
                            <div class="custom-overlay">
                                <b>${store.csName}</b>
                                <p>${store.csNick}<br>
                                태그: ${store.csTag}<br>
                                <img src="${starImage}" alt="별점" style="height: 20px; margin: 7px;"><br>
                                도로명 주소: ${store.roadAddressName}<br>
                                일반 주소: ${store.addressName}</p>
                                <button class="custom-button" onclick="redirectToPage('${encodeURIComponent(store.csName || '')}', '${encodeURIComponent(store.csNick || '')}', '${encodeURIComponent(store.csTag || '')}', '${encodeURIComponent(store.roadAddressName || '')}', '${encodeURIComponent(store.addressName || '')}', ${store.rating || 0})">상세보기</button>
                            </div>`;

                        var overlay = new kakao.maps.CustomOverlay({
                            content: overlayContent,
                            position: marker.getPosition(),
                            yAnchor: 1.35,
                            map: null
                        });

                        // 마커 클릭 시 오버레이 표시
                        kakao.maps.event.addListener(marker, 'click', function () {
                            overlay.setMap(map);
                            console.log('오버레이 표시됨:', store); // 디버깅용 로그
                        });

                        // 지도 클릭 시 오버레이 숨기기
                        kakao.maps.event.addListener(map, 'click', function () {
                            overlay.setMap(null);
                        });

                        // 생성된 마커를 배열에 추가
                        markers.push(marker);
                    });
                })
                .catch(error => {
                    document.getElementById('loading').style.display = 'none';
                    console.error('Error loading stores:', error);
                });
        }

        // 초기 로딩 시 마커 생성
        loadStores(map.getCenter().getLat(), map.getCenter().getLng());

        // 지도 이동 완료 이벤트
        kakao.maps.event.addListener(map, 'idle', function () {
            var center = map.getCenter();
            loadStores(center.getLat(), center.getLng());
        });
    });
</script>



    <!-- Footer -->
		<div id="footer-wrapper">
			<footer id="footer" class="container">
				<div class="row">
					<section class="문의하기">
						<h3>문의하기</h3>
						<p>주소 : 광주 서구 월드컵4강로 27 <br />
							이메일 : bigdataty@gmail.com<br />
							전화번호 : 010-6235-5916</p>
					</section>
				</div>
			</div>
			<div class="row">
				<div class="col-12">
					<div id="copyright">
						<ul class="menu">
							<li>&copy; 태버지 주식회사</li>
							<li>Design: <a href="./ty.html">태버지와 아이들</a></li>
						</ul>
					</div>
				</div>
			</div>
		</footer>
		</div>

	</div>

	<script src="./jquery.min.js"></script>
	<script src="./jquery.dropotron.min.js"></script>
	<script src="./browser.min.js"></script>
	<script src="./breakpoints.min.js"></script>
	<script src="./util.js"></script>
	<script src="./main.js"></script>
</body>
</html>
