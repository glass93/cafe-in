import os
import os.path
import time
from flask import Flask
from flask_restful import Resource, Api,request
from flask_restful import reqparse
from flask_cors import CORS
import json
import time
from common.db import db,engine,db_session
import wordcount
app = Flask(__name__)
CORS(app)
api = Api(app)
app.config['JSON_AS_ASCII'] = False

@app.teardown_appcontext
def shutdown_session(exception=None):
    db_session.remove()
    print("sutdown")


@app.route('/flask/word')
def getwordcount():
    cafeId = request.args.get('cafeid')
    print('getword')
    result = engine.execute('select content from %s.review where cafe_id = "%s";' % (db['database'], cafeId))
    rows = result.fetchall()
    dicList = []
    for v in rows:
        dicList.append(v[0])
    String = " ".join(dicList)
    count = wordcount.count(String)        
    return json.dumps(count, ensure_ascii=False).encode('utf-8')


# 예시 코드1 함수로 바로 url 등록
@app.route('/users/<userEmail>/groups', methods=['GET'])
def getAllGroups(userEmail):
    uid = (db.engine.execute('Select id From %s.user WHERE googleEmail = "%s";' %
           (db['database'], userEmail))).first()[0]
    result = db.engine.execute(
        'Select * From %s.group WHERE uid = %d order by favorites desc;' % (db['database'], uid))
    # 튜플들의 리스트로 결과 행들 받아옴
    rows = result.fetchall()
    dicList = []
    for v in rows:
        dicList.append({'id': v[0], 'name': v[1], 'favorites': v[2]})
        json.dumps(dicList)
    return json.dumps(dicList)



# 예시코드2 클래스로 빼서 등록 (차후 모듈화에 더 편리)
class SampleClass(Resource):
    def get(self, userEmail):
        return "test"

    def post(self, userEmail):
        return "test"


# 예시코드2 클래스로 뺄 경우 이렇게 url 등록
api.add_resource(SampleClass, '/users/<userEmail>')

# 서버 실행
if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)
