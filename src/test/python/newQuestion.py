# -*coding: utf-8 -*
# run.id 새로 생성( 새로운 주제)

import sys
import io


sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

content1 = sys.argv[1] if len(sys.argv) > 1 else ""

from openai import OpenAI
import os
from dotenv import load_dotenv
import time

# 환경변수 불러오기.
env_path = os.path.join(os.path.dirname(__file__), '..', '.env')
load_dotenv(dotenv_path=env_path)



# API 키 로드
api_key = os.getenv("OPENAI_API_KEY")
client = OpenAI(api_key=api_key)


# 어시스턴트 아이디 만들기
my_assistant = client.beta.assistants.create(
    instructions="당신은 서울여행 가이드의 모든 정보를 알고있는 로봇입니다. 질문자가 원하는 답변 형식에 맞춰서 답변합니다.",
    name="SeoulGuide1",
    tools=[{"type": "code_interpreter"}],
    model="gpt-4o",
)
# print(my_assistant)


assistant_id = my_assistant.id

# 스레드 아이디 만들기
# Threads -> create thread
empty_thread = client.beta.threads.create()
# print(empty_thread)

thread_id = empty_thread.id



# 메세지 만들기
# Messages -> create message
thread_message = client.beta.threads.messages.create(
    thread_id,
    role="user",
    content= content1,
)
# print(thread_message.content[0].text.value)

# 런 아이디 만들기
# run은 thread라는 하나의 채팅방 내에서 하나의 대화 주제를 가리킨다. 그래서 질문을 이어서 받을때는 같은 run.id가 필요하고
# 새로운 질문을 할 때에는 새로운 run.id를 사용한다.
# Runs -> create run
run = client.beta.threads.runs.create(
    thread_id=thread_id,
    assistant_id=assistant_id
)

# print(run)

run_id1 = run.id

# 우리가 말을하면 기다리는 시간이 필요하다.
# Runs -> Retrieve run
while True:
    run = client.beta.threads.runs.retrieve(    # 말그대로 검색을 하는 것이라서 주석처리 안해도됨.
        thread_id=thread_id,
        run_id=run_id1
    )
    if run.status == "completed":
        break
    else:
        time.sleep(3)
    # print(run.status)      # status='completed'라고 나온다. 실패하면 failed, 진행중일때는 in_progress가 나온다.


# Messages -> List messages
thread_messages = client.beta.threads.messages.list(thread_id)
print(thread_messages.data[0].content[0].text.value)

