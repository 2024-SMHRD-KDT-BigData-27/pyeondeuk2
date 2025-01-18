<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="ko">

<head>
<link rel="stylesheet" href="resources/css/review.css">
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>리뷰 작성</title>
<!-- jQuery 추가 -->
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>

<script>
	// URL 파라미터 읽기
	function getQueryParam(param) {
		const urlParams = new URLSearchParams(window.location.search);
		return urlParams.get(param);
	}

	$(document).ready(function() {
		// URL 파라미터 읽기
		const csName = getQueryParam('csName');
		const csNick = getQueryParam('csNick');
		const csTag = getQueryParam('csTag');
		const roadAddressName = getQueryParam('roadAddressName');
		const addressName = getQueryParam('addressName');
		const rating = getQueryParam('rating');

		// HTML 요소에 값 설정
		$('#csName').text(csName || '알 수 없는 편의점');
		$('#rating').text(rating || '0');
		$('#csNick').text(csNick || '별명 없음');
		$('#csTag').text(csTag || '태그 없음');
		$('#roadAddressName').text(roadAddressName || '도로명 주소 없음');
		$('#addressName').text(addressName || '주소 없음');
	});
</script>

<body>
	<h3 style="text-align: center;">
		지점명: <span id="csName"></span>
	</h3>
	<h4 style="text-align: center;">
		별명: <span id="csNick"></span>
	</h4>
	<h4 style="text-align: center;">
		태그: <span id="csTag"></span>
	</h4>
	<h4 style="text-align: center;">
		도로명 주소: <span id="roadAddressName"></span>
	</h4>
	<h4 style="text-align: center;">
		일반 주소: <span id="addressName"></span>
	</h4>
	<h4 style="text-align: center;">
		평균 별점: <span id="rating"></span>
	</h4>

	<div class="star_rating">
		<span class="star" data-value="1"></span> <span class="star"
			data-value="2"></span> <span class="star" data-value="3"></span> <span
			class="star" data-value="4"></span> <span class="star" data-value="5"></span>
	</div>

	<textarea class="star_box" placeholder="해당 편의점에 대한 리뷰 내용을 작성해주세요."></textarea>
	<input type="submit" class="btn02" value="리뷰 등록" style="color: white;" />

	<!-- 리뷰와 별점이 표시될 공간 -->
	<div id="reviewDisplay">
		<h4 style="text-align: center;">작성된 리뷰</h4>
		<ul id="reviewList"></ul>
	</div>


	<script src="./star.js"></script>
</body>

</html>
