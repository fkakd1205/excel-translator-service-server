# excel-translator-server
엑셀변환기 서버

<br />

1. 시퀀스
2. 성능 테스트

<br />


### 1. 시퀀스
<img src="https://github.com/user-attachments/assets/bcf9da5a-5124-4101-aa37-61aaa2dc3e1c" width="600px" />

<br />
<br />

### 2. 성능 테스트
엑셀 다운로드 시, 기존의 다운로드 방식(`XSSF`)과 변경된 방식(`SXSSF`, 지정된 row만큼만 메모리에서 작업하는 방식)을 비교 

<br />

Case1. 5초 동안 진행하며, 1초마다 30명의 유저가 연속으로 파일을 요청했을 때

Case2. 1초에 100명의 유저가 동시에 파일을 요청했을 때

<br />
<br />

Case1)
1. XSSF을 활용해 업로드 & XSSF을 활용해 다운로드
<img src="https://github.com/user-attachments/assets/f3fec764-a791-45b0-8575-f2ce79d52748" width="700px" />

2. XSSF을 활용해 업로드 & SXSSF을 활용해 다운로드
<img src="https://github.com/user-attachments/assets/29953ecf-bc48-4547-987a-a0c75e37dc13" width="700px" />

<br />

→ 평균 응답 속도는 1300ms에서 270ms로 단축, TPS는 18.6에서 63.1로 향상

<br />

Case2)
1. XSSF을 활용해 업로드 & XSSF을 활용해 다운로드
<img src="https://github.com/user-attachments/assets/c0032202-63a4-4070-93f6-012f713f434a" width="700px" />
<figure style="margin-left: 50%; transform: translateX(-50%); width: 1100px; max-width: 1100px;">
  <div style="display: flex; justify-content: space-between; flex-wrap: wrap;">
    <img src="https://github.com/user-attachments/assets/0d1bd461-add5-46ac-ba5a-9fc107a16e9e" width="30%" />
    <img src="https://github.com/user-attachments/assets/20a5f067-5532-4d70-b33d-99e0da89d119" width="30%" />
  </div>
</figure>

<br />

2. XSSF을 활용해 업로드 & SXSSF을 활용해 다운로드
<img src="https://github.com/user-attachments/assets/a1ffd127-f9c0-41b5-a974-73b3cceb58a7" width="700px" />
<figure style="margin-left: 50%; transform: translateX(-50%); width: 1100px; max-width: 1100px;">
  <div style="display: flex; justify-content: space-between; flex-wrap: wrap;">
    <img src="https://github.com/user-attachments/assets/a17f7ad4-c3c7-4d7e-9ea3-3a780ea45497" width="30%" />
    <img src="https://github.com/user-attachments/assets/a0474972-b20e-435f-88ae-233b1cb6dfbb" width="30%" />
  </div>
</figure>

<br />

→ 평균 응답 속도는 2.8s에서 1.2s로 단축, TPS는 19.1에서 37.2로 향상

→ CPU 자원과 JVM GC 대상인 Eden과 Old 영역도 덜 사용하고 있는 것을 확인

<br />
<br />



