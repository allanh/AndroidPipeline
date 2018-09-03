# Pipeline
Pipeline implementation with unit test cases

## Usage
Get origin IP from HttpBin server.

```java
HttpItem okHttpItem = new HttpItem.Builder(this)
        .url("https://httpbin.org/ip")
        .dataModel(HttpBinGetIpItem.class) // for response
        .timeout(5000L)
        .get();

PipelineManager.getInstance().doPipeline(
        new BaseHttpTaskList(),
        okHttpItem,
        new HttpBinGetIpCallback());
```

## Unit Test Cases (in pipeline/src/test/java/com/fuhu/pipeline)
Test that thet is as expected.

```java
@Test
public void runProcess() throws Exception {
    //arrange
    mockStatic(RequestBody.class);
    JSONObject jsonObject = mock(JSONObject.class);
    HttpItem httpItem = spy(HttpItem.class);
    httpItem.setRequestJson(jsonObject);
    BuildOkHttpRequestBodyTask buildOkHttpClientTask = spy(BuildOkHttpRequestBodyTask.class);
    RequestBody requestBody = spy(RequestBody.class);

    when(RequestBody.create(any(MediaType.class), any(String.class))).thenReturn(requestBody);

    //act
    buildOkHttpClientTask.process(httpItem);

    //assert
    verifyStatic();
    RequestBody.create(any(MediaType.class), any(String.class));
    verify(httpItem).setOkHttpRequestBody(any(RequestBody.class));
}
```
