package com.ttm.tlrb.utils;

/**
 * Created by 李晓伟 on 2016/5/26.
 * 数据同步操作
 */
public class DataSyncUtil {

    /*public synchronized static void syncRedBombData(final String userName) {
        Subscriber<BaseEn> subscriber = new Subscriber<BaseEn>() {
            @Override
            public void onCompleted() {
                int count =
                DataSupport
                        .where("isSync = false and userName = " + userName)
                        .count(RedBomb.class);
                if(count <= 0){//所有数据都已同步到服务器

                }
            }

            @Override
            public void onError(Throwable e) {
                //出现异常，通知用户重新同步

            }

            @Override
            public void onNext(BaseEn baseEn) {

            }
        };
        //1、获取本地未同步的数据,将本地未同步的数据上传到服务器
        Observable.create(new Observable.OnSubscribe<List<RedBomb>>() {
            @Override
            public void call(Subscriber<? super List<RedBomb>> subscriber) {
                ContentValues userNameCV = new ContentValues();
                userNameCV.put("userName", userName);
                //将本地userName为空的数据，更新为当前用户的数据
                DataSupport.updateAll(RedBomb.class, userNameCV, "userName = ?", "");
                int i = 0;
                while (true) {
                    //获取本地未同步的数据，每次50条
                    List<RedBomb> redBombs =
                            DataSupport
                                    .where("isSync = false and userName = " + userName)
                                    .limit(APIService.BATCH_LIMIT_COUNT)
                                    .offset(i * APIService.BATCH_LIMIT_COUNT)
                                    .find(RedBomb.class);
                    //如果onNext被执行，说明已经同步成功，否则会调用onError
                    subscriber.onNext(redBombs);
                    //将同步标记位置为true
                    if(redBombs != null){
                        final long[] ids = new long[redBombs.size()];
                        ContentValues isSyncCV = new ContentValues();
                        userNameCV.put("isSync", true);
                        for (long id : ids) {
                            DataSupport.update(RedBomb.class, isSyncCV, id);
                        }
                    }

                    i++;
                    if (redBombs == null || redBombs.size() < APIService.BATCH_LIMIT_COUNT) {
                        break;
                    }
                }
                //数据上传完成，调用onCompleted
                subscriber.onCompleted();
            }
        })
        .flatMap(new Func1<List<RedBomb>, Observable<BaseEn>>() {
            @Override
            public Observable<BaseEn> call(List<RedBomb> redBombs) {
                return postRedBombBatch(redBombs);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);

        //2、将服务器的数据分批次获取（每次100条？）
        //3、将获取到的数据和本地数据一一比较，是否已经存在，不存在，插入本地数据库
    }

    *//**
     * 将数据上传服务器
     * @param redBombs
     * @return
     *//*
    private static Observable<BaseEn> postRedBombBatch(List<RedBomb> redBombs){
        Gson gson = new Gson();
        Map<String, List<RequestBatch>> map = new HashMap<>();
        List<RequestBatch> requestBatches = new ArrayList<>(1);
        RequestBatch request = new RequestBatch();
        request.setMethod("POST");
        request.setPath("/1/classes/RedBomb");
        request.setBody(gson.toJson(redBombs));
        requestBatches.add(request);
        map.put("requests", requestBatches);

        RequestBody requestBody = RequestBody.create(Constant.JSON, gson.toJson(map));
        APIService apiService = APIManager.getInstance().getAPIService();
        return apiService.postBatch(requestBody);
    }*/
}
