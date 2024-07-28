# inventory-backend

## Getting started

1. Generating SSL certificate
```shell
keytool -genkeypair -alias inventoryapi -keyalg RSA -keysize 2048 -storetype PKCS12 -keystore keystore.p12 -validity 3650
```

2. Saving keystore and db password
```shell
cd backend
echo -e "KEYSTORE_PASSWORD=your_keystore_password\nDB_PASSWORD=your_db_password" > .env
```

## ERA model

![era_diag](https://github.com/user-attachments/assets/6d9aa3d3-844e-4fbb-9762-525a875dc53d)
