from parse import load_dataframes
import pandas as pd
import shutil
import numpy as np


def sort_stores_by_score(dataframes, n=20, min_reviews=30):
    """
    Req. 1-2-1 각 음식점의 평균 평점을 계산하여 높은 평점의 음식점 순으로 `n`개의 음식점을 정렬하여 리턴합니다
    Req. 1-2-2 리뷰 개수가 `min_reviews` 미만인 음식점은 제외합니다.
    """
    stores_reviews = pd.merge(
        dataframes["stores"], dataframes["reviews"], left_on="id", right_on="store"
    )
    scores_group = stores_reviews.groupby(["store", "store_name"])
    scores = scores_group.mean().sort_values(by=["score"], ascending=False)
    scores = scores.loc[scores["review_cnt"] >= min_reviews]
    return scores.head(n=n).reset_index()


def get_most_reviewed_stores(dataframes, n=20):
    """
    Req. 1-2-3 가장 많은 리뷰를 받은 `n`개의 음식점을 정렬하여 리턴합니다
    """
    stores_reviews = dataframes["stores"]
    scores = stores_reviews.sort_values(by=["review_cnt"], ascending=False)

    return scores.head(n=n).reset_index()


def get_most_active_users(dataframes, n=20):
    """
    Req. 1-2-4 가장 많은 리뷰를 작성한 `n`명의 유저를 정렬하여 리턴합니다.
    """
    users_reviews = pd.merge(
        dataframes["users"], dataframes["reviews"], left_on="id", right_on="user"
    )
    users_group = users_reviews.groupby(["user"]).size().reset_index().rename(columns={0:'cnt'})
    users = users_group.sort_values(by=["cnt"], ascending=False)

    return users.head(n=n).reset_index()


def get_user_store(dataframes):
    """
    Req. 1-4-1 유저 - 음식점 행렬을 생성합니다.
    """
    stores_reviews = pd.merge(
        dataframes["stores"], dataframes["reviews"], left_on="id", right_on="store"
    )

    user_list = list(set(stores_reviews['user'].values.tolist()))
    user_list.sort()
    store_list = list(set(stores_reviews['store_name'].values.tolist()))

    df = pd.DataFrame(data=np.nan, index=user_list, columns=store_list)

    user_group = stores_reviews.sort_values(by='user').groupby(['user', 'store_name']).mean().loc[:, 'score']

    for index, score in user_group.items():
        user, store_name = index
        df.loc[user, store_name] = score

    print(df)

def main():
    data = load_dataframes()

    term_w = shutil.get_terminal_size()[0] - 1
    separater = "-" * term_w

    stores_most_scored = sort_stores_by_score(data)
    most_reviewed_stores = get_most_reviewed_stores(data)
    most_active_users = get_most_active_users(data)

    print("[최고 평점 음식점]")
    print(f"{separater}\n")
    for i, store in stores_most_scored.iterrows():
        print(
            "{rank}위: {store}({score}점)".format(
                rank=i + 1, store=store.store_name, score=store.score
            )
        )
    print(f"\n{separater}\n\n")

    print("[가장 많은 리뷰를 받은 음식점]")
    print(f"{separater}\n")
    for i, store in most_reviewed_stores.iterrows():
        print(
            "{rank}위: {store}({review_cnt}개)".format(
                rank=i + 1, store=store.store_name, review_cnt=store.review_cnt
            )
        )
    print(f"\n{separater}\n\n")

    print("[가장 많은 리뷰쓴 유저]")
    print(f"{separater}\n")
    for i, user in most_active_users.iterrows():
        print(
            "{rank}위: {id}({count}개)".format(
                rank=i + 1, id=user.user, count=user.cnt
            )
        )
    print(f"\n{separater}\n\n")

    print("[유저-음식점 행렬]")
    print(f"{separater}\n")
    get_user_store(data)
    print(f"\n{separater}\n\n")


if __name__ == "__main__":
    main()
