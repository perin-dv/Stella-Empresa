# Stella D'Italia — App Empresa

Aplicativo Android nativo do lado operacional do ecossistema Stella D'Italia.

![Android](https://img.shields.io/badge/Android-3DDC84?style=flat-square&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=flat-square&logo=kotlin&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=flat-square&logo=firebase&logoColor=black)

## Visão geral

Este repositório concentra o aplicativo destinado à operação da empresa, complementando o app de cliente do projeto Stella.

A estrutura atual inclui telas e fluxos para autenticação, home operacional, cadastro de novos produtos, promoções e histórico de promoções.

## Tecnologias

- Kotlin
- Android nativo
- Android Views / XML
- ViewBinding
- Firebase Authentication
- Firebase Realtime Database
- Cloud Firestore
- Firebase Storage
- Room
- LiveData / ViewModel
- Navigation Components
- Glide / Picasso
- Lottie
- Groupie

## Requisitos

- Android Studio
- JDK 21
- Android SDK 35
- ambiente Firebase configurado

## Build local

```bash
git clone https://github.com/perin-dv/Stella-Empresa.git
cd Stella-Empresa
./gradlew assembleDebug
```

No Windows:

```powershell
.\gradlew.bat assembleDebug
```

## Arquitetura do produto

O projeto Stella é dividido em dois aplicativos:

- **Stella Cliente** — experiência de compra, catálogo, carrinho, pedidos e perfil;
- **Stella Empresa** — operação interna, produtos e promoções.

## Projeto relacionado

- [Stella Cliente](https://github.com/perin-dv/Stella-Cliente)
- [Portfólio de Guilherme Perin](https://guilherme-perin.vercel.app)

## Autor

**Guilherme Perin**  
Software • IA • Cloud • Automação
