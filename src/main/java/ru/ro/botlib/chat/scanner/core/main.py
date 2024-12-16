import asyncio
import csv

from pyrogram import Client, enums

from __private_constants__ import CHAT_ID, API_ID, API_HASH


async def main():
    async with Client("my_account", API_ID, API_HASH) as app:
        await app.send_message("me", "Greetings from **Pyrogram**!")

        users = [
            ['ID', 'Nick', 'Name', 'PhoneNumber', 'Status', 'JoinedDate']
        ]

        async for member in app.get_chat_members(chat_id=CHAT_ID, filter=enums.ChatMembersFilter.BOTS):
            user = member.user

            user_id = user.id

            user_nick = f"@{user.username}" if user.username is not None else ""

            user_first_name = user.first_name if user.first_name is not None else ""
            user_last_name = user.last_name if user.last_name is not None else ""
            user_name = f"{user_first_name} {user_last_name}"

            user_phone_number = user.phone_number if user.phone_number is not None else ""

            user_status = str(member.status).split(".")[1]

            user_joined_date = member.joined_date
            user_joined_date_str = f"{user_joined_date.day}/{user_joined_date.month}/{user_joined_date.year}" if user_joined_date is not None else ""

            users.append([user_id, user_nick, user_name, user_phone_number, user_status, user_joined_date_str])

        with open('output.csv', mode='w', newline='') as file:
            csv_writer = csv.writer(file)
            csv_writer.writerows(users)


asyncio.run(main())
