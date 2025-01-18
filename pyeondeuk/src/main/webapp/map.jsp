<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>편의점 지도</title>
<script
	src="https://dapi.kakao.com/v2/maps/sdk.js?appkey=818db477dcf4c15dd62010e4fcb787b6&autoload=false"></script>
<style>
@font-face {
	font-family: 'Ownglyph_ryuttung-Rg';
	src:
		url('https://fastly.jsdelivr.net/gh/projectnoonnu/2405-2@1.0/Ownglyph_ryuttung-Rg.woff2')
		format('woff2');
	font-weight: normal;
	font-style: normal;
}

.nickname {
    font-family: 'Ownglyph_ryuttung-Rg', sans-serif; 
    font-size: 18px; 
    color: #ff69b4;
    font-weight: 575; 
    margin: 0; 
    padding: 5px;
    display: inline-block;
    text-align: center; 
}

#map {
	width: 100%;
	height: 600px;
}

.custom-overlay {
	background-color: rgba(255, 255, 255, 0.7);
	backdrop-filter: blur(5px);
	padding: 13px;
	border-radius: 8px;
	text-align: center;
	box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
	font-size: 14px;
	color: #4e555b;
}

.custom-overlay b {
	display: block;
	font-size: 16px;
	margin-bottom: 8px;
}

.custom-overlay p {
	margin: 2px 0;
	line-height: 1.45
}

.custom-overlay .custom-button {
	margin-top: 6px;
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
	top: 300px;
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
	<h1>편의점 지도</h1>
	<div id="loading">로딩 중...</div>
	<div id="map"></div>

	<script>
        // 상세보기 페이지로 이동하는 함수
        function redirectToPage(csSeq, csName, csNick, csTag, roadAddressName, addressName, rating) {
            console.log('redirectToPage 호출됨:', { csSeq, csName, csNick, csTag, roadAddressName, addressName, rating });

            csNick = csNick;
            csTag = csTag;
            roadAddressName = roadAddressName;
            addressName = addressName;
            rating = rating;

            const url = `review.jsp?csSeq=${csSeq}csName=${encodeURIComponent(csName)}&csNick=${encodeURIComponent(csNick)}&csTag=${encodeURIComponent(csTag)}&roadAddressName=${encodeURIComponent(roadAddressName)}&addressName=${encodeURIComponent(addressName)}&rating=${rating}`;
            console.log('생성된 URL:', url);

            window.location.href = url;
        }

        kakao.maps.load(function () {
            var mapContainer = document.getElementById('map');
            var mapOption = {
                center: new kakao.maps.LatLng(35.1595, 126.8526), // 초기 중심 좌표
                level: 3 // 확대 수준
            };

            var map = new kakao.maps.Map(mapContainer, mapOption);
            var markers = []; // 기존 마커를 관리할 배열

            var markerImages = {
                1: "./resources/images/emart24_marker.png",
                2: "./resources/images/cu_marker.png",
                3: "./resources/images/gs25_marker.png",
                4: "./resources/images/seven_marker.png"
            };

            function loadStores(lat, lng) {
                document.getElementById('loading').style.display = 'block';

                fetch('<%=request.getContextPath()%>/getNearbyStores?latitude=' + lat + '&longitude=' + lng + '&radius=0.7')
                    .then(response => response.json())
                    .then(data => {
                        document.getElementById('loading').style.display = 'none';
                        markers.forEach(marker => marker.setMap(null));
                        markers = [];

                        data.forEach(store => {
                        	 var rating = store.rating || 0; // 기본값 0
                        	    var starImage;

                        	    // 별점 이미지 결정
                        	    if (rating >= 4.5) starImage = './resources/images/5star.png';
                        	    else if (rating >= 3.5) starImage = './resources/images/4star.png';
                        	    else if (rating >= 2.5) starImage = './resources/images/3star.png';
                        	    else if (rating >= 1.5) starImage = './resources/images/2star.png';
                        	    else starImage = './resources/images/1star.png';

                        	    var markerImageUrl = markerImages[store.brandSeq];
                        	    var markerImage = new kakao.maps.MarkerImage(
                        	        markerImageUrl,
                        	        new kakao.maps.Size(38, 50)
                        	    );
                          
                            var marker = new kakao.maps.Marker({
                                map: map,
                                position: new kakao.maps.LatLng(store.latitude, store.longitude),
                                image: markerImage
                            });

                            var overlayContent = document.createElement('div');
                            overlayContent.className = 'custom-overlay';

                            overlayContent.innerHTML = `
                                <b>${store.csName}</b>
                                <img src="${starImage}" alt="별점" style="height: 20px;"><br>
                                <span class="nickname">' ${store.csNick || '리뷰가 없어요 ㅠ'} '</span><br>
                                <p>${store.csTag ? `store.csTag}<br>` : ''}
                                ${store.roadAddressName ? `도로명주소: ${store.roadAddressName}<br>` : ''}
                                ${store.addressName ? `지번주소: ${store.addressName}` : ''}
                                </p>
                            `;

                            var button = document.createElement('button');
                            button.className = 'custom-button';
                            button.innerText = '상세보기';

                            // 버튼 클릭 시 이벤트 전파 방지 및 상세보기 함수 호출
                            button.addEventListener('click', function (event) {
                                console.log('상세보기 버튼 클릭됨'); // 디버깅 로그
                                redirectToPage(
                                	store.csSeq,
                                    store.csName,
                                    store.csNick || '',
                                    store.csTag || '',
                                    store.roadAddressName || '',
                                    store.addressName || '',
                                    store.rating || 0
                                );
                            });

                            overlayContent.appendChild(button);

                            var overlay = new kakao.maps.CustomOverlay({
                                content: overlayContent,
                                position: marker.getPosition(),
                                clickable: true,
                                yAnchor: 1.35,
                                map: null
                            });

                            // 지도 클릭 시 오버레이 닫기
                            kakao.maps.event.addListener(map, 'click', function () {
                                overlay.setMap(null);
                            });

                            // 마커 클릭 시 오버레이 표시
                            kakao.maps.event.addListener(marker, 'click', function () {
                                overlay.setMap(map);
                            });

                            markers.push(marker);
                        });
                    })
                    .catch(error => {
                        document.getElementById('loading').style.display = 'none';
                        console.error('Error loading stores:', error);
                    });
            }

            loadStores(map.getCenter().getLat(), map.getCenter().getLng());

            kakao.maps.event.addListener(map, 'idle', function () {
                var center = map.getCenter();
                loadStores(center.getLat(), center.getLng());
            });
        });
    </script>
</body>
</html>
