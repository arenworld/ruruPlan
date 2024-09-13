import sys
import mysql.connector
from rapidfuzz import fuzz
import io
import re

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

# 커서 생성
cursor = conn.cursor()

# si_gun_gu에 맞는 데이터 가져오기 (LIKE 사용)
query = "SELECT place_id, title, address, si_gun_gu FROM ruru_place_info WHERE si_gun_gu LIKE %s"
cursor.execute(query, (si_gun_gu,))

# 쿼리 결과 가져오기
rows = cursor.fetchall()

# 문자열 전처리 함수 (특수문자 및 공백 제거)
def preprocess_text(text):
    # 한글, 숫자, 공백만 남기고 제거
    text = re.sub(r'[^\w\s]', '', text)
    # 공백도 제거
    return text.replace(" ", "").strip()

# 입력된 title과 DB title을 비교하는 유사도 비교 함수
def calculate_similarity(input_title, db_title):
    # 문자열 전처리
    input_title_clean = preprocess_text(input_title)
    db_title_clean = preprocess_text(db_title)
    
    # 유사도 계산 (token_set_ratio 사용)
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
