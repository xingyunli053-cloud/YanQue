# 雁雀教学运营管理端

基于 Vue 3、Vite 和 Element Plus 构建的后台管理前端。

## 功能

- 管理端登录
- 用户分页、详情、新增、修改、删除（联调真实后端）
- 校区管理
- 课程管理
- 课程详情管理
- 班级管理
- 运营总览

教学管理模块目前使用浏览器 LocalStorage 保存演示数据，后端接口就绪后可在 `src/api` 中增加对应请求并替换页面数据源。

## 本地运行

后端默认运行在 `http://localhost:8080`，前端开发服务器通过 Vite 代理访问 `/yq-admin`。

```bash
npm install
npm run dev
```

生产构建：

```bash
npm run build
```

## 后端联调约定

- 登录：`POST /yq-admin/api/sysUser/login`
- 用户列表：`GET /yq-admin/api/sysUser`
- 用户详情：`GET /yq-admin/api/sysUser/{id}`
- 新增用户：`POST /yq-admin/api/sysUser`
- 修改用户：`PUT /yq-admin/api/sysUser/{id}`
- 删除用户：`DELETE /yq-admin/api/sysUser/{id}`

登录后，前端会自动携带 `Authorization`，并根据后端当前规则生成 `X-Timestamp`、`X-Nonce` 和 `X-Sign`。

签名原文为：

```text
METHOD
URI
QUERY
TIMESTAMP
NONCE
```
