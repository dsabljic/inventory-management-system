# inventory-backend

[frontend](https://github.com/dsabljic/inventory-management-system-frontend)

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

![Screenshot from 2024-07-17 15-57-23](https://github.com/user-attachments/assets/e4f20af9-39de-4f3b-a7cb-3209604aa71c)
