CREATE TABLE IF NOT EXISTS roles (
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   name VARCHAR(50) NOT NULL,
   furigana VARCHAR(50) NOT NULL,
   postal_code VARCHAR(50) NOT NULL,
   address VARCHAR(255) NOT NULL,
   phone_number VARCHAR(50) NOT NULL,
   birthday DATE,
   occupation VARCHAR(50),
   email VARCHAR(255) NOT NULL UNIQUE,
   password VARCHAR(255) NOT NULL,
   role_id INT NOT NULL,
   enabled BOOLEAN NOT NULL,
   stripe_customer_id VARCHAR(255) UNIQUE,
   created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
   updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
   FOREIGN KEY (role_id) REFERENCES roles (id)
);

CREATE TABLE IF NOT EXISTS verification_tokens(
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	user_id INT NOT NULL UNIQUE,
	token  VARCHAR(255)	NOT NULL,
	created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
	updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)	
);

--店舗用のテーブル
CREATE TABLE IF NOT EXISTS restaurants (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,      -- 主キー、自動採番
    name VARCHAR(50) NOT NULL,                       -- 店舗名（必須）
    image VARCHAR(255),                              -- 店舗画像（任意）
    description TEXT NOT NULL,                       -- 説明（必須）
    lowest_price INT NOT NULL,                       -- 最低価格（必須）
    highest_price INT NOT NULL,                      -- 最高価格（必須）
    postal_code VARCHAR(50) NOT NULL,                -- 郵便番号（必須）
    address VARCHAR(255) NOT NULL,                   -- 住所（必須）
    opening_time TIME NOT NULL,                      -- 開店時間（必須）
    closing_time TIME NOT NULL,                      -- 閉店時間（必須）
    seating_capacity INT NOT NULL,                   -- 座席数（必須）
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,  -- 作成日時（必須、デフォルト現在時刻）
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新日時（必須、自動更新）
);

-- カテゴリ用のテーブル
CREATE TABLE IF NOT EXISTS categories (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,      -- 主キー、自動採番
    name VARCHAR(50) NOT NULL                       -- 店舗カテゴリ名（必須）
 );
 
-- 店舗-カテゴリ用の中間テーブル（多-多テーブルをつなぐ）
 CREATE TABLE IF NOT EXISTS category_restaurant (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    category_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,-- 更新日時（必須、自動更新）
    UNIQUE (restaurant_id, category_id),--同じお店とカテゴリの組み合わせを二度と入れない
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id), -- 他のテーブルとつなげるルール（外部キー）
    FOREIGN KEY (category_id) REFERENCES categories (id) -- 他のテーブルとつなげるルール（外部キー）
);
 
-- 定休日を管理するテーブル
CREATE TABLE IF NOT EXISTS regular_holidays (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, -- 主キー（自動採番）
    day VARCHAR(50) NOT NULL,                   -- 定休日（例：月曜日、火曜日、不定休など）
    day_index INT                               -- カレンダー制御用の番号（例：0=日曜, 1=月曜...）
);

-- 店舗と定休日を結びつける中間テーブル
CREATE TABLE IF NOT EXISTS regular_holiday_restaurant (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    regular_holiday_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    UNIQUE (restaurant_id, regular_holiday_id), -- 2つのカラムをセットにしてユニークにする という意味
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
    FOREIGN KEY (regular_holiday_id) REFERENCES regular_holidays (id)
);

-- 会社概要テーブル
CREATE TABLE IF NOT EXISTS companies (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,         -- 主キー
    name VARCHAR(50) NOT NULL,                         -- 会社名
    postal_code VARCHAR(50) NOT NULL,                  -- 郵便番号
    address VARCHAR(255) NOT NULL,                     -- 所在地
    representative VARCHAR(50) NOT NULL,               -- 代表者
    establishment_date VARCHAR(50) NOT NULL,           -- 設立年月日
    capital VARCHAR(50) NOT NULL,                      -- 資本金
    business VARCHAR(255) NOT NULL,                    -- 事業内容
    number_of_employees VARCHAR(50) NOT NULL,          -- 従業員数
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,                -- 作成日時
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新日時
);

-- 利用規約テーブル
CREATE TABLE IF NOT EXISTS terms (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,        -- 主キー
    content TEXT NOT NULL,                             -- 利用規約の本文
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,                -- 作成日時
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 更新日時
);

-- レビュー用テーブル

CREATE TABLE IF NOT EXISTS reviews (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    content TEXT NOT NULL,
    score INT NOT NULL,
    restaurant_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE (restaurant_id, user_id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- reservationsテーブル
CREATE TABLE IF NOT EXISTS reservations (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    reserved_datetime DATETIME NOT NULL,
    number_of_people INT NOT NULL,
    restaurant_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS favorites (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    restaurant_id INT NOT NULL,
    user_id INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE (restaurant_id, user_id),
    FOREIGN KEY (restaurant_id) REFERENCES restaurants (id),
    FOREIGN KEY (user_id) REFERENCES users (id)
);

-- PW resetテーブル
CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);


