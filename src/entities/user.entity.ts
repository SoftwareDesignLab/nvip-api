import {
    Entity,
    PrimaryGeneratedColumn,
    Column,
    CreateDateColumn,
} from 'typeorm';

@Entity()
export class User {
    @PrimaryGeneratedColumn({ name: 'user_id' })
    userID: number;

    @Column({ nullable: true })
    token: string;

    @Column()
    userName: string;

    @Column()
    firstName: string;

    @Column()
    lastName: string;

    @Column()
    email: string;

    @Column({ nullable: false })
    passwordHash: string;

    @Column()
    roleId: number;

    @CreateDateColumn({
        name: 'token_expiration_date',
        type: 'timestamp',
        nullable: true,
    })
    expirationDate: Date;

    @CreateDateColumn({ type: 'timestamp', nullable: true })
    lastLoginDate: Date;
}