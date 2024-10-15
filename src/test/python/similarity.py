import sys
import mysql.connector
from rapidfuzz import fuzz      # 문자열 유사도를 비교하는 라이브러리(두 문자열 간의 유사도 비교할 때 사용)
import io   # 입출력 제어 모듈, 여기서는 기본 출력 스트림을 UTF-8로 설정하기 위해 사용
import re   # 정규 표현식 사용하기위해 문자열 처리하는 모듈. 여기서는 특수문자와 공백 제거 위해 사용
 
sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

# Java에서 넘어온 인자 받기
title = sys.argv[1].replace(" ", "") if len(sys.argv) > 1 else ""  # title의 공백 제거
si_gun_gu = sys.argv[2] if len(sys.argv) > 2 else ""

# DB 연결 (MySQL)
conn = mysql.connector.connect(
    host="localhost",        # MySQL 서버 호스트 (보통 로컬에서는 'localhost')
    user="root",             # MySQL 사용자 이름
    password="root",         # MySQL 비밀번호
    database="ruru"          # 연결할 데이터베이스 이름
)

# 커서 생성( SQL쿼리 결과를 가져오는 객체 생성)
cursor = conn.cursor()

# si_gun_gu에 맞는 데이터 가져오기 (LIKE 사용)
query = "SELECT place_id, title_kr, address_kr, si_gun_gu FROM ruru_place_info WHERE si_gun_gu LIKE %s"
cursor.execute(query, (si_gun_gu,))

# 쿼리 결과 가져오기 (쿼리 결과의 모든 행을 리스트로 가져옴)
rows = cursor.fetchall()

# 문자열 전처리 함수 (특수문자 및 공백 제거)
def preprocess_text(text):
    # 한글, 숫자, 공백만 남기고 제거
    text = re.sub(r'[^\w\s]', '', text)
    # 공백도 제거
    return text.replace(" ", "").strip()

# 입력된 title과 DB title을 비교하는 유사도 비교 함수
def calculate_similarity(input_title, db_title):
    # 먼저 공백 제거(preprocess_text)
    input_title_clean = preprocess_text(input_title)
    db_title_clean = preprocess_text(db_title)
    
    # 유사도 계산 (token_set_ratio 사용 -> 퍼센트로 결과값 도출)
    return fuzz.token_set_ratio(input_title_clean, db_title_clean)

# 정확한 일치 우선 처리
def exact_match(input_title, db_title):
    input_title_clean = preprocess_text(input_title)
    db_title_clean = preprocess_text(db_title)
    return input_title_clean == db_title_clean

# 유사도가 가장 높은 데이터를 찾기 위한 변수 초기화
best_match = None
highest_similarity = 0

# 모든 행을 순회하며 유사도를 계산
for row in rows:
    place_id, db_title, address, si_gun_gu = row
    
    # 1. 정확한 일치 우선 확인
    if exact_match(title, db_title):
        best_match = {"place_id": place_id, "similarity": 100}
        break  # 정확한 일치가 있으면 더 이상 비교할 필요 없음
    
    # 2. 정확한 일치가 없으면 유사도 기반 비교
    similarity = calculate_similarity(title, db_title)
    
    # 유사도가 현재까지 가장 높은 값이면 업데이트
    if similarity > highest_similarity:
        highest_similarity = similarity
        best_match = {"place_id": place_id, "similarity": similarity}

# 결과 출력 (정확한 일치 또는 유사도가 가장 높은 값 출력)
if best_match and best_match["similarity"] >= 60:
    print(best_match['place_id'])     # best_match["similarity"]
else:
    print("일치하는 정보 없음")

# MySQL 연결 닫기
conn.close()
