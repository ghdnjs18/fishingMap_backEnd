![표지](https://github.com/ghdnjs18/fishingLoad_backEnd/assets/70429434/457f716e-fbe1-40c7-9dc2-dd97e001d53e)

# Fishing Load 
### 낚시꾼들간에 낚시하는 장소의 위치와 잡히는 어종 정보등을 공유를 할 수 있는 커뮤니티 서비스
#

#
## 프로젝트 소개 📢
- 프로젝트 설명 :
    - 낚시 스팟 공유 사이트
- 프로젝트 이름 :
    - Fishing Load
- 구현 기능 및 핵심 목표 :
  
    1.**로그인 및 회원, 게시물, 댓글 어드민 서비스 개발**
    - Spring Security를 이용한 로그인 구현
    - JWT를 이용한 인증 처리 구현
    - 회원 어드민 서비스 구현
    - 게시글 어드민 서비스 구현
    - 댓글 및 대댓글 어드민 서비스 구현
      
    </br>
    
   2.**게시물 이미지 처리**
    - AWS S3를 이용한 이미지 처리
    - AWS CloudFront를 이용한 이미지 보안 처리
      
    </br>

   3.**CI / CD 환경 구축**
    - Git Action을 이용한 자동 빌드 환경 구축
    - AWS CodeDeploy를 이용한 자동 배포 환경 구축
      
    </br>
    
   4.**프론트 개발자와 협업**
    - Swagger를 이용한 API 문서 관리
    - API 통신을 이용한 데이터 처리
    - CORS 문제 해결
 
    </br>
    
- 프로젝트 기간 : 
    - 2023.09.14 ~ 2023.09.27
- 팀원 : <br>

| - | 이름 | GITHUB |
|--|--|--|
| BE | 김호원 | https://github.com/ghdnjs18 |
| BE | 양승민 | https://github.com/sarakyang |

#

## 주요 기능 소개 🔍

### 전국 포인트 및 나만의 포인트
1. Naver Web Dynamic Map API 활용하여 포인트 등록 시 입력한 주소로 마커 생성
2. 마커 클릭 시 지도 오른쪽 영역에 선택한 마커의 기본 정보와 대표 이미지 출력
3. 지도 오른쪽 상단의 SEA/FRESH WATER 버튼을 통해 민물/바다 카테고리로 분류된 마커들이 출력

![image](https://github.com/ghdnjs18/fishingLoad_backEnd/assets/70429434/c8e6f81a-4f5e-49d3-ac97-b76ed8424845)

### 전국 포인트 및 나만의 포인트
1. 선택된 포인트의 위치로 지도가 업데이트 되고 등록할 때 입력한 정보들이 간단하게 출력
2. 해당 포인트에 좋아요 및 댓글을 입력할 수 있고, 로그인 한 유저의 토큰 값을 통해 좋아요를 한 유저인지 여부를 구분 처리함

![image](https://github.com/ghdnjs18/fishingLoad_backEnd/assets/70429434/ddd72765-86e9-481d-9099-ea2c42941a61)

### 포인트 등록페이지
1. 선택된 테마에 따른 다른 종류들의 물고기 리스트들이 버튼 형태로 출력되고 클릭 시 토클 형식으로 선택 여부를 판별하도록 처리
2. Daum Post-Code를 사용해서 주소를 입력받고, KaKao Map API의 Geocoder를 통해 해당 주소의 좌표값을 구한 뒤 서버로 전달

![image](https://github.com/ghdnjs18/fishingLoad_backEnd/assets/70429434/5faf5bb3-5ce3-4f61-acd7-4e129a007504)

#

## ERD 🔗
<details>
<summary> 펼쳐보기 </summary>
<div markdown="1">  
    
  ![테이블 (1)](https://github.com/ghdnjs18/fishingLoad_backEnd/assets/70429434/2457e8b8-1eaf-4f1a-9254-632e7cc372a8)


</div>
</details>
