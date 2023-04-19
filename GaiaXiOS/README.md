
验证
```shell
pod spec lint ./GaiaXiOS.podspec --verbose --allow-warnings  --skip-import-validation
```

发布
```shell
pod trunk push GaiaXiOS.podspec --skip-import-validation --verbose --allow-warnings
```