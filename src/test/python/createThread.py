# -*coding: utf-8 -*


from openai import OpenAI
import os
from dotenv import load_dotenv


# 환경변수 불러오기.
env_path = os.path.join(os.path.dirname(__file__), '..', '.env')
load_dotenv(dotenv_path=env_path)

# API 키 로드
api_key = os.getenv("OPENAI_API_KEY")
client = OpenAI(api_key=api_key)

# 스레드 아이디 만들기
# Threads -> create thread
empty_thread = client.beta.threads.create()
print(empty_thread.id)