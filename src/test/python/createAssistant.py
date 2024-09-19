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

# 어시스턴트 아이디 만들기
my_assistant = client.beta.assistants.create(
    instructions="당신은 서울여행 가이드의 모든 정보를 알고있는 로봇입니다. 질문자가 원하는 답변 형식에 맞춰서 답변합니다.",
    name="SeoulGuide2",
    tools=[{"type": "code_interpreter"}],
    model="gpt-4o",     # gpt-3.5-turbo-1106    # gpt-4o
)  
print(my_assistant)